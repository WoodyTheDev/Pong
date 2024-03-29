import { Component, OnInit, Injectable, ElementRef, ViewChild, HostListener } from '@angular/core';
import { AuthService } from '../Services/auth.service';
import { Router } from '@angular/router';
import { RxStompService } from '@stomp/ng2-stompjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { APIConnection } from '../APIConnection';
import { Ball, Paddle, GameField } from './game-objects';

@Component({
  selector: 'app-game-state',
  templateUrl: './game-state.component.html',
  styleUrls: ['./game-state.component.scss'],
})
@Injectable({
  providedIn: 'root',
})
export class GameStateComponent implements OnInit {
  @ViewChild('gamefield') gamefield!: ElementRef;

  @HostListener('document:keydown', ['$event'])
  handleKeydown(event: KeyboardEvent) {
    if (event.key === 'ArrowUp' || event.key === 'w' || event.key === 'W') {
      this.sendPaddleDirection(1);
    } else if (event.key === 'ArrowDown' || event.key === 's' || event.key === 'S') {
      this.sendPaddleDirection(-1);
    }
  }

  @HostListener('document:keyup', ['$event'])
  handleKeyup(event: KeyboardEvent) {
    if (event.key === 'ArrowUp' || event.key === 'w' || event.key === 'W' ||
      event.key === 'ArrowDown' || event.key === 's' || event.key === 'S') {
      this.sendPaddleDirection(0);
    }
  }



  // Benötigte Klassenvariablen Initialisieren
  public gameFieldData: GameField | null = null;
  public player1Paddle: Paddle | null = null;
  public player2Paddle: Paddle | null = null;
  public ball: Ball | null = null;
  public actualGameID!: any;

  //Initialisieren der benötigten Variablen für das Spiel (Ausgabe im HTML - Template)
  public gameInfoData: any;
  public paddleData: any[] = [];
  public BallData: any[] = [];
  public scoreData: any[] = [{ content: { player1Score: 0, player2Score: 0 } }];



  // Stomp-Abonnement für die Komponente
  private stompSubscription: any;
  //Nutzerdaten aus dem local Storage holen, wenn dieser gesetzt ist
  private email = JSON.parse(localStorage.getItem('APIUserData') || '{}').email;
  private password = JSON.parse(localStorage.getItem('APIUserData') || '{}').password;

  //WebSocket Verbindung
  private url!: string;

  //Playerinformations
  public playerInfo: { id: number, playername: string } | null;
  public isLoggedIn: boolean = false;
  public playerName!: { player1: string; player2: string; };


  //Designelemente zum Bearbeiten der Optik in der Game-State selbst
  public isDropdownOpen: boolean = false;
  public isCanvasHidden: boolean = false;
  public isGameInfoHidden: boolean = false;
  public imageNav: string | null = null; // imageUrl deklarieren


  constructor(private authService: AuthService, private router: Router,
    private rxStompService: RxStompService,
    private http: HttpClient,
    private APIConnection: APIConnection) {
    this.playerInfo = null;
    this.playerName = { player1: '', player2: '' };
    this.url = this.APIConnection.getAPIWebSocket();
  };

  ngOnInit() {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
    } else {

      this.loadProfilPicture();
      this.authService.showGame$.subscribe(showGame => {
        this.isCanvasHidden = !showGame;
        this.isGameInfoHidden = !showGame;
      });


      const storedPlayerInfo = localStorage.getItem('player_info');
      this.playerInfo = storedPlayerInfo ? JSON.parse(storedPlayerInfo) : null;
    }
  }

  /**
   * Websocket Verbindungen
   */

  //Game Observe / Subscribe
  private subscribe(): void {
    const GameURL = '/game/' + this.email + '/init';
    console.log("GameURL:" + GameURL);
    this.stompSubscription = this.rxStompService.watch(GameURL).subscribe((message) => {
      console.log('Ingame !');
      //Zur Ausgabe auf dem Bildschirm
      const GameData = JSON.parse(message.body);
      const gameId = GameData.id;
      this.actualGameID = gameId;
      this.gameInfoData = GameData;

      //Initialisierung der benötigten Objekte. 

      this.gameFieldData = GameField.getInstance(GameData.gamefield)
      this.ball = Ball.getInstance(GameData.gamefield.ball);
      this.player1Paddle = Paddle.getPlayer1Instance(GameData.gamefield.player1Paddle)
      this.player2Paddle = Paddle.getPlayer2Instance(GameData.gamefield.player2Paddle)
      this.playerName = { player1: GameData.player1?.playername, player2: GameData.player2?.playername };

      //Subscibe auf Paddle Movments
      this.rxStompService.watch(`/game/${gameId}/paddleMovement`).subscribe((paddleMovement) => {
        const PaddleData = JSON.parse(paddleMovement.body);
        const PaddleDataOut = { title: 'Paddle Daten', content: PaddleData };
        this.paddleData.push(PaddleDataOut);

        if (PaddleData.paddleOwner === 'PLAYER1') {
          if (this.player1Paddle === null) {
            this.player1Paddle = Paddle.getPlayer1Instance(PaddleData);
          } else {
            this.player1Paddle.updatePaddleData(PaddleData);
          }
        } else if (PaddleData.paddleOwner === 'PLAYER2') {
          if (this.player2Paddle === null) {
            this.player2Paddle = Paddle.getPlayer2Instance(PaddleData);
          } else {
            this.player2Paddle.updatePaddleData(PaddleData);
          }
        }
      });

      //Subscribe auf Ball Movements
      this.rxStompService.watch(`/game/${gameId}/ballMovement`).subscribe((ballMovement) => {
        const BallData = JSON.parse(ballMovement.body);
        const BallDataOut = { title: 'Ball Daten', content: BallData };
        this.BallData.push(BallDataOut);

        // Wenn noch nicht erstellt (eigentlich nicht möglich), dann erst erstellen, ansonsten alle Informationen die kommen Updaten. 
        if (this.ball === null) {
          this.ball = Ball.getInstance(BallData);
        } else {
          this.ball.updateBallData(BallData);
        }

      });

      //Subscribe auf ScoreData
      this.rxStompService.watch(`/game/${gameId}/score`).subscribe((score) => {
        const ScoreData = JSON.parse(score.body);
        const ScoreDataOut = { title: 'Score Daten', content: ScoreData };
        this.scoreData.push(ScoreDataOut);
        //console.log("Score : "  +JSON.stringify(ScoreData, null, 2));

        if (ScoreData.winner !== null) {
          alert("WINNER, disconnect websocket");
          this.rxStompService.deactivate();
          this.router.navigate(['/game-state']);
        }
      });

    });
    this.rxStompService.connected$.subscribe((connected) => {
      console.log('WebSocket Connected aus Subscibe:', connected);
    });
  }


  //Eingeloggten nutzer zum Spiel hinzufügen
  private connectUser(): void {
    //Wenn man AuthTokens übergeben kann
    const token = this.authService.getAccessToken();
    const headerInfo = this.authService.getHeaderAuthorization();
    console.log(token);
    console.log(headerInfo);
    this.rxStompService.configure({
      brokerURL: `${this.url}/ws?email=${this.email}&password=${this.password}`,
    });

    //Verbindung aktivieren
    this.rxStompService.activate();

    // Nach erfolgreicher Verbindung
    this.rxStompService.connected$.subscribe((frame) => {
      console.log('Connected to WebSocket aus ConnectUser', frame);
      this.subscribe();
      this.rxStompService.publish({ destination: '/pong/game' });
    }, (error) => {
      console.log('Error connecting to WebSocket:', error);
    });
    this.rxStompService.watch(`response`).subscribe((response) => {
      console.log("Antwort", JSON.parse(response.body));
    })
  }
  startGame() {
    this.connectUser();
  }

  // Sende Paddle vom eingeloggten Spieler
  sendPaddleDirection(direction: number) {
    const destination = `/pong/game/${this.actualGameID}/paddle`;
    const headers = {};
    // Nachricht senden
    const directionAsString = direction.toString();
    this.rxStompService.publish({ destination, headers, body: directionAsString });
  }


  private manuallyDisconnectWebSocket(): void {
    // Manuell die WebSocket-Verbindung trennen
    this.rxStompService.deactivate();
    console.log("Verbindung zum Websocket gestoppt");
  }

  private manuallyReconnectWebSocket(): void {
    // Aktivieren Sie die WebSocket-Verbindung
    this.rxStompService.activate();
    console.log('Verbindung zum Websocket gestartet');
  }

  deleteWebSocketConn(): void {
    this.manuallyDisconnectWebSocket();
  }

  reconnectSocketConn(): void {
    this.manuallyReconnectWebSocket();
  }

  openDropdown() {
    this.isDropdownOpen = true;
  }

  closeDropdown() {
    this.isDropdownOpen = false;
  }

  ShowAccessToken() {
    console.log("Access Token from GameState:", this.authService.getAccessToken());
  }

  loadProfilPicture() {
    this.authService.profilePicture$.subscribe(profilePicture => {
      if (profilePicture) {
        this.imageNav = URL.createObjectURL(profilePicture);
      }
    });
  }
  editProfile() {
    this.router.navigate(['/user']);
  }


  logout() {
    const userData = JSON.parse(localStorage.getItem('APIUserData') || '{}');
    const data = { email: userData.email };
    // Setze den Bearer-Token im Header

    console.log(userData.email);

    this.http.post<any>(this.APIConnection.getAPIAuth() + "/logout", data, { headers: this.authService.getHeaderAuthorization(), observe: 'response' }).subscribe(
      response => {
        console.log('Erfolgreich ausgeloggt:', response.status);
        localStorage.removeItem('access_Token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('access_token_pong');
        localStorage.removeItem('APIUserData');
        localStorage.removeItem('player_info');
        this.router.navigate(['/login']);
      },
      error => {
        console.error('Fehler beim Ausloggen:', error.status);
        localStorage.removeItem('access_Token');
        localStorage.removeItem('refresh_token');
        localStorage.removeItem('access_token_pong');
        localStorage.removeItem('APIUserData');
        localStorage.removeItem('player_info');
      }
    );
  }
}