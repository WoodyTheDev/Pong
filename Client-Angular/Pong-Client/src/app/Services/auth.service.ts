import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private accessToken: string | null = null;
  private refreshToken: string | null = null;
  // Wird benötigt, da in der GameState Komponente neben dem Header auch das Canvas element ist. Also schauen ob sich der Zustand hier ändert. 
  private showGameSource = new BehaviorSubject<boolean>(true);
  showGame$ = this.showGameSource.asObservable();

  // Profilbild Observen, ob sich der Zustand ändert
  private profilePictureSource = new BehaviorSubject<Blob | null>(null);
  profilePicture$ = this.profilePictureSource.asObservable();

  constructor() { }
  setTokens(tokens: { accessToken: string; refreshToken: string }): void {
    this.accessToken = tokens.accessToken;
    this.refreshToken = tokens.refreshToken;
    localStorage.setItem("access_Token", tokens.accessToken);
    localStorage.setItem("refresh_token", tokens.refreshToken);
  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  getRefreshToken(): string | null {
    return this.refreshToken;
  }

  //Bild konvertieren
  convertArrayBufferToBlob(arrayBuffer: ArrayBuffer) {
    const blob = new Blob([arrayBuffer], { type: 'image/jpeg' });
    // Bild speichern und Änderungen in das Observable übertragen
    this.setProfilePicture(blob);
  }

  setProfilePicture(image: Blob | null) {
    this.profilePictureSource.next(image);
  }

  //Header für Schnittstellen ausgeben
  getHeaderAuthorization() {
    return { 'Authorization': `Bearer ${this.getAccessToken()}` };
  }

  isLoggedIn(): boolean {
    return !!this.accessToken;
  }

  setGame(value: boolean) {
    this.showGameSource.next(value);
  }
  getShowGame() {
    return this.showGame$;
  }
}
