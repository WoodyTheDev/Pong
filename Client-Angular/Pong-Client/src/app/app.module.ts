import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing-module';
import { AppComponent } from './app.component';
import { AuthService } from './Services/auth.service'; 
import { LoginComponent } from './login/login.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { GameStateComponent } from './game-state/game-state.component';
import { RegisterComponent } from './register/register.component';
import { HistoryComponent } from './history/history.component';
import { UserComponent } from './user/user.component';
import { RxStompService } from '@stomp/ng2-stompjs';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    GameStateComponent,
    RegisterComponent,
    HistoryComponent,
    UserComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    BrowserModule
  ],
  providers: [AuthService,RxStompService],
  bootstrap: [AppComponent]
})
export class AppModule { }
