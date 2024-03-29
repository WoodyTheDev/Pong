import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './Services/auth.service';


@Component({
  selector: 'app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  template: '<router-outlet></router-outlet>'
})
export class AppComponent {
  title = 'Pong-Client';
  accessToken: string | null = null;
  refreshToken: string | null = null;

  constructor(private router: Router,  private authService: AuthService) {
    this.router.navigate(['/login']);
  }

  ngOnInit() {
    //Prüfen ob Accestoken aus authService bereitsteht
    this.accessToken = this.authService.getAccessToken();
    this.refreshToken = this.authService.getRefreshToken();
    if (this.accessToken || this.refreshToken) {
      // Wenn ein Token vorhanden ist, navigiere zur GameState-Komponente
      this.router.navigate(['/game-state']);
    } else {
      // Wenn kein Token vorhanden ist, navigiere zur Login-Komponente
      this.router.navigate(['/login']);
    }
  }
}
