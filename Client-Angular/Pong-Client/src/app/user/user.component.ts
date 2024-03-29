import { Component, OnInit } from '@angular/core';
import { HttpClient} from '@angular/common/http';
import { Router } from '@angular/router'
import { AuthService } from '../Services/auth.service';
import { APIConnection } from '../APIConnection';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent {

  private UserToken: string | null = null;
  public UserPicture: string | null = null;
  public UserEmail: string | null = null;
  public UserName: string | null = null;
  public imageBlob: Blob | null = null;


  public profileImage: string | undefined

  //Ausgaben in HTML-Template 
  public errorMessage: string | null = null;
  public infoMessage: string | null = null;
  public successMessage: string | null = null;

  constructor(private http: HttpClient, private router: Router, private authService: AuthService, private APIConnection: APIConnection) {
    this.UserToken = this.authService.getAccessToken();
  }


  ngOnInit(): void {
    this.toggleCanvasDisplay();

    //Observable für das Profilbild
    this.authService.profilePicture$.subscribe(imageBlob => {
      if (imageBlob) {
        this.profileImage = URL.createObjectURL(imageBlob);
      } else {
        this.profileImage = undefined;
      }
    });
  }


  // Daten aus dem Formular an Funktion übergeben und an PongAPIUrl übergeben mittels Post
  patchUser(formData: any) {
    console.log('Patch User - Data:', formData);
    const headers = this.authService.getHeaderAuthorization();
    this.http.patch<any>(this.APIConnection.getAPIUser(), formData, { headers: headers}).subscribe (
      response => {
        console.log(response);
         this.successMessage = "Passwort wurde erfolgreich geändert";
      }, 
      error => {
        console.log(error);
        this.errorMessage = "Der aktuelle Benutzer wurde nicht erkannt \nServer:" + error.error.message ;
      }
    );
  }

  // Image vom Server downloaden
  downloadImage() {
    const headers = this.authService.getHeaderAuthorization();
    this.http.get(this.APIConnection.getAPIUser() + "/image", { headers: headers, responseType: 'arraybuffer' }).subscribe(
      response => {
        // Bilddaten als Blob
        //console.log(response);
        this.convertArrayBufferToBlob(response);
      },
      error => {
        console.error('Fehler beim Herunterladen des Bildes:', error);
      }
    );
  }

  //Image hochladen
  uploadImage() {
    // Zuerst Prüfen ob Image als Blob vorliegt. 
    if (this.imageBlob) {
      const headers = this.authService.getHeaderAuthorization();
      const formData = new FormData();
      //Body / PostData anpassen. 
      formData.append('image', this.imageBlob, 'imageuser');
      this.http.post(this.APIConnection.getAPIUser() + "/image", formData, { headers: headers }).subscribe(
        response => {
          this.downloadImage();
          this.successMessage = 'Erfolgreich ein Profilbild hochgeladen';
        },
        error => {
          this.errorMessage = 'Fehler beim Hochladen des Bildes. Bitte versuchen Sie es erneut.';
        }
      );
    } else {
      this.infoMessage = 'Bitte wählen Sie zuerst ein Bild aus.';
    }
  }


  // Methode zum Konvertieren des ausgewählten Bilds in Blob damit es beim Uploaden entsprechend verarbeitet vorliegt.
  convertImageToBlob(event: any) {
    //Erste Eintrag nehmen (Nur einer existent)
    const file = event.target.files[0];
    if (file) {
      this.imageBlob = file;
      console.log('Bild erfolgreich in Blob umgewandelt:', this.imageBlob);
    } else {
      console.error('Bitte wählen Sie zuerst ein Bild aus.');
    }
  }

  //Methode um Antwort des Server wieder in Bild darzustellen. 
  convertArrayBufferToBlob(arrayBuffer: ArrayBuffer) {
    const blob = new Blob([arrayBuffer], { type: 'image/jpeg' });
    // Bild speichern und Änderungen in das Observable übertragen
    this.authService.setProfilePicture(blob);
  }

  //Display off
  toggleCanvasDisplay() {
    this.authService.setGame(false);
  }

  //Messages beim schließen entfernen
  clearMessages() {
    this.errorMessage = '';
    this.infoMessage = '';
    this.successMessage = '';
  }

}