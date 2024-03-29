import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../Services/auth.service';
import { APIConnection } from '../APIConnection';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  private PongAPIUrl: string | null = null;
  public successMessage: string | null = null;
  public errorMessage: string | null = null;
  public profileImage: string | undefined;
  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService,
    private APIConnection: APIConnection
  ) {
    // API SchnittStelle aus Service APIConnection
    this.PongAPIUrl = this.APIConnection.getAPIAuth();
  }

  ngOnInit(): void {
    // Hier das Observable Abbonieren für das Profilbild
    this.authService.profilePicture$.subscribe(imageBlob => {
      if (imageBlob) {
        this.profileImage = URL.createObjectURL(imageBlob);
      } else {
        this.profileImage = undefined;
      }
    });
  }
  //Daten an Server senden
  postData(data: any, link: string): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      this.http.post<any>(this.PongAPIUrl + link, data, { observe: 'response' }).subscribe(
        response => {
          console.log('Erfolgreich:', response.status);
          console.log(response);
          const accessToken = response.body?.access_token;
          const refreshToken = response.body?.refresh_token;

          // Speichere Access und Refresh Token im AuthService
          this.authService.setTokens({ accessToken, refreshToken });

          // Speichere im Local Storage alle Player-Informationen die man erhält (ID und Playername)
          localStorage.setItem('player_info', JSON.stringify(response.body?.player));
          localStorage.setItem('APIUserData', JSON.stringify(data));
          //Image Beziehen
          this.downloadImage();
          this.successMessage = "Erfolgreich eingeloggt";
          resolve(response.status);
        },
        error => {
          console.error('Fehler :', error.status);
          this.errorMessage = "Login Fehlgeschlagen";
          reject(error.status);
        }
      );
    });
  }

  downloadImage() {
    const headers = this.authService.getHeaderAuthorization();
    this.http.get(this.APIConnection.getAPIUser() + "/image", { headers: headers, responseType: 'arraybuffer' }).subscribe(
      response => {
        console.log(response);
        this.authService.convertArrayBufferToBlob(response);
      },
      error => {
        console.error('Fehler beim Herunterladen des Bildes:', error);
      }
    );
  }


  //Daten aus der Form empfangen
  LoginSubmit(formData: any) {
    console.log(formData);
    this.postData(formData, "/authenticate")
      .then(status => {
        if (status === 200) {
          this.router.navigate(['/game-state']);
        }
      })
      .catch(error => {
        console.error("Fehler beim Login:", error);
      });
  }

  GoRegisterSubmit() {
    this.router.navigate(['/register']);
  }
}
