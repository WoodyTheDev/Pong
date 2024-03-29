import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router'
import { APIConnection } from '../APIConnection';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  //Ausgaben in HTML-Template 
  public errorMessage: string | null = null;
  public infoMessage: string | null = null;
  public successMessage: string | null = null;

  constructor(
    private http: HttpClient,
    private router: Router,
    private APIConnection: APIConnection) { }

  //Daten an Server senden
  postData(data: any, adress: string): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      this.http.post<any>(this.APIConnection.getAPIAuth() + adress, data, { observe: 'response' }).subscribe(
        response => {
          console.log('Erfolgreich:', response.status);
          resolve(response.status);
          const accessToken = response.body?.access_token;
          localStorage.setItem('access_token_pong', accessToken);
        },
        error => {
          console.error('Fehler :', error.status);
          reject(error.status);
        }
      );
    });
  }

  //Nach Absenden Button 
  RegisterSubmit(formData: any) {
    if (this.checkEmail(formData.email)) {
      this.postData(formData, "/register")
        .then(status => {
          if (status === 200) {
            //alert("Erfolgreich Registriert");
            this.successMessage = "Vielen Dank für deine Registrierung, du wirst zur Anmeldung weitergeleitet"
            //this.router.navigate(['/game-state']);
            setTimeout(() => {
              location.reload();
            }, 3000);
          } else {

          }
        })
        .catch(error => {
          console.error("Fehler beim Login:", error);
        });
    } else {
      console.error("E-Mail nicht korrekt");
      this.errorMessage = "Bitte gib eine gültige E-Mail Adresse an"
    }

  }


  private checkEmail(email: string): boolean {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailPattern.test(email);
  }
}
