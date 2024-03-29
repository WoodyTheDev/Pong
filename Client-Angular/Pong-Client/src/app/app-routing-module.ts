import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component' ;
import { GameStateComponent } from './game-state/game-state.component';
import { UserComponent } from './user/user.component';
import { HistoryComponent } from './history/history.component';
const routes: Routes = [
  { path: 'game-state', component: GameStateComponent },
  { path: 'user', component: UserComponent},
  { path: 'history', component: HistoryComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
