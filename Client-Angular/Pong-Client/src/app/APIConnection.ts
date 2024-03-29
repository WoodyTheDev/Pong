import { Injectable } from '@angular/core';



@Injectable({
  providedIn: 'root'
})
export class APIConnection {
    private APIAuth =  "http://lyra.et-inf.fho-emden.de:19036/api/v1/auth";
    private APIUser = "http://lyra.et-inf.fho-emden.de:19036/api/v1/users";
    private APIPlayer = "http://lyra.et-inf.fho-emden.de:19036/api/v1/player";
    private APIGame: string | null = null;
    private APIWebSocket= "ws://lyra.et-inf.fho-emden.de:19036";

    constructor( ) {}

    
    getAPIAuth () {
        return this.APIAuth;
    }
    getAPIUser () {
        return this.APIUser;
    }
    getAPIGame () {
        return this.APIGame;
    }
    getAPIPlayer () {
        return this.APIPlayer;
    }
    getAPIWebSocket (){
        return this.APIWebSocket;
    }
}

