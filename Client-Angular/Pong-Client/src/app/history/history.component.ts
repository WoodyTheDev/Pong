import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../Services/auth.service';
import { APIConnection } from '../APIConnection';
import { Router } from '@angular/router';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.scss'],
})
export class HistoryComponent implements OnInit {
  players: any[] = [];
  pagedPlayers: any[] = [];
  currentPage: number = 1;
  pageSize: number = 10;
  playerStats: any[] = [];
  mostPlayedDay: string = '';
  sortOrder: 'ascending' | 'descending' | 'none' = 'none';

  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: AuthService,
    private APIConnection: APIConnection
  ) { }

  ngOnInit(): void {
    this.showHistory();
  }

  showHistory() {
    const headers = this.authService.getHeaderAuthorization();
    this.http
      .get(this.APIConnection.getAPIPlayer(), {
        headers: headers,
        responseType: 'text',
      })
      .subscribe(
        (response) => {
          console.log('API Response:', response);
          this.players = JSON.parse(response);
          // Sortieren nach Erstellungsdatum
          this.players.sort((a, b) => new Date(b.createDate).getTime() - new Date(a.createDate).getTime());

          this.setPagedPlayers();
          this.calculatePlayerStats();
          this.findMostPlayedDay();
          this.findMostPlayedOpponent();

        },
        (error) => {
          console.error('Fehler beim Herunterladen der History Daten:', error);
          if (error instanceof HttpErrorResponse) {
            console.error('Status:', error.status);
            console.error('Fehlermeldung vom Server:', error.error);
          }
        }
      );
  }

  // Festlegung der Spieler pro Seite
  setPagedPlayers() {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    this.pagedPlayers = this.players.slice(startIndex, startIndex + this.pageSize);
  }

  // Navigieren zur vorherigen Seite
  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.setPagedPlayers();
    }
  }

  // Navigieren zur nächsten Seite
  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.setPagedPlayers();
    }
  }

  // Gesamtanzahl der Seiten
  get totalPages(): number {
    return Math.ceil(this.players.length / this.pageSize);
  }


  // Errechnet die Spielerstatistiken (Gewonnene und verlorene Spiele)
  calculatePlayerStats() {

    this.playerStats = [];
    this.players.forEach((game) => {
      const winner = game.gameScore.winner === 'PLAYER1' ? game.player1 : game.player2;
      const loser = game.gameScore.winner === 'PLAYER1' ? game.player2 : game.player1;
      const winnerIndex = this.playerStats.findIndex((player) => player.name === winner);
      const loserIndex = this.playerStats.findIndex((player) => player.name === loser);

      if (winnerIndex === -1) {
        this.playerStats.push({ name: winner, wins: 1, losses: 0 });
      } else {
        this.playerStats[winnerIndex].wins++;
      }

      if (loserIndex === -1) {
        this.playerStats.push({ name: loser, wins: 0, losses: 1 });
      } else {
        this.playerStats[loserIndex].losses++;
      }
    });
  }


  // Findet den Tag mit den meisten gespielten Spielen 
  findMostPlayedDay() {
    // Wochentage zählen
    const daysCount: { [key: string]: number } = {
      Sunday: 0,
      Monday: 0,
      Tuesday: 0,
      Wednesday: 0,
      Thursday: 0,
      Friday: 0,
      Saturday: 0
    };

    // Anzahl der Spiele pro Wochentag
    this.players.forEach((game) => {
      const gameDate = new Date(game.createDate);
      const dayOfWeek = gameDate.toLocaleDateString('en-US', { weekday: 'long' });
      daysCount[dayOfWeek]++;
    });

    let maxCount = 0;
    let mostPlayedDay = '';

    // Tag mit der höchsten Anzahl 
    for (const day in daysCount) {
      if (daysCount[day] > maxCount) {
        maxCount = daysCount[day];
        mostPlayedDay = day;
      }
    }

    // Setze den am häufigsten gespielten Tag
    this.mostPlayedDay = mostPlayedDay;
  }


  // Findet den Gegner gegen den am meisten gespielt wurde
  findMostPlayedOpponent() {
    const opponentsCount: { [key: string]: number } = {};

    // Anzahl der Spiele pro Gegner
    this.players.forEach((game) => {
      const opponent = game.gameScore.winner === 'PLAYER1' ? game.player2 : game.player1;
      opponentsCount[opponent] = (opponentsCount[opponent] || 0) + 1;
    });

    let maxCount = 0;
    let mostPlayedOpponent = '';

    // Spieler mit der höchsten Anzahl
    for (const opponent in opponentsCount) {
      if (opponentsCount[opponent] > maxCount) {
        maxCount = opponentsCount[opponent];
        mostPlayedOpponent = opponent;
      }
    }

    return mostPlayedOpponent;
  }


  // Tabellen Sortierung
  sortTable() {
    if (this.sortOrder === 'ascending') {
      this.players.sort((a, b) => new Date(a.createDate).getTime() - new Date(b.createDate).getTime());
    } else if (this.sortOrder === 'descending') {
      this.players.sort((a, b) => new Date(b.createDate).getTime() - new Date(a.createDate).getTime());
    }
    // Update pagedPlayers after sorting
    this.setPagedPlayers();
  }
  //Display off
  hideCanvasDisplay() {
    this.authService.setGame(false);
  }
}