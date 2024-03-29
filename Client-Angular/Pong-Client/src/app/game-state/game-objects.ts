//  Klasse repräsentiert den Spielball
export class Ball {
  private static instance: Ball | null = null;

  private positionX: number;
  private positionY: number;
  private width: number;
  private height: number;
  private duration: number;

  //Instanzen erstellen
  private constructor(data: any) {
    this.positionX = data.position.x || 0;
    this.positionY = data.position.y || 0;
    this.width = data.width || 0;
    this.height = data.height || 0;
    this.duration = data.timeForDistanceInSeconds || 0;
  }

  //Hier kann auf die Instanz zugegriffen werden. 
  public static getInstance(data: any): Ball {
    if (Ball.instance === null) {
      Ball.instance = new Ball(data);
    }
    return Ball.instance;
  }

   // Aktualisieren der Ballposition und der Animationsdauer
  public updateBallData(data: any): void {
    this.positionX = data.position.x - data.width / 2;
    this.positionY = data.position.y - data.height / 2;
    this.duration = data.timeForDistanceInSeconds;
  }

  public get PositionX(): number {
    return this.positionX;
  }

  public get PositionY(): number {
    return this.positionY;
  }

  public get Duration(): number {
    return this.duration;
  }

  public get Width(): number {
    return this.width;
  }

  public get Height(): number {
    return this.height;
  }
}
// Klasse repräsentiert den Paddle
export class Paddle {
  private static player1Instance: Paddle | null = null;
  private static player2Instance: Paddle | null = null;

  private positionX: number;
  private positionY: number;
  private width: number;
  private height: number;
  private owner: string;

  //Constructor
  private constructor(data: any, owner: string) {
    this.positionX = data.position.x || 0;
    this.positionY = data.position.y || 0;
    this.width = data.width || 0;
    this.height = data.height || 0;
    this.owner = owner;
  }

  //Auf die Istanzen zugreifen
  public static getPlayer1Instance(data: any): Paddle {
    if (Paddle.player1Instance === null) {
      Paddle.player1Instance = new Paddle(data, 'PLAYER1');
    }
    return Paddle.player1Instance;
  }

  public static getPlayer2Instance(data: any): Paddle {
    if (Paddle.player2Instance === null) {
      Paddle.player2Instance = new Paddle(data, 'PLAYER2');
    }
    return Paddle.player2Instance;
  }

  //Position von Paddleupdaten
  public updatePaddleData(data: any): void {
    this.positionX = data.position.x - data.width / 2;
    this.positionY = data.position.y - data.height / 2;
  }

  public get PositionX(): number {
    return this.positionX;
  }

  public get PositionY(): number {
    return this.positionY;
  }

  public get Width(): number {
    return this.width;
  }

  public get Height(): number {
    return this.height;
  }

  public get Owner(): string {
    return this.owner;
  }
}
// Spielfeld
export class GameField {
  private static instance: GameField | null = null;

  private width: number;
  private height: number;

  private constructor(data: any) {
    this.width = data.width || 0;
    this.height = data.height || 0;
  }

  public static getInstance(data: any): GameField {
    if (GameField.instance === null) {
      GameField.instance = new GameField(data);
    }
    return GameField.instance;
  }

  public get Width(): number {
    return this.width;
  }

  public get Height(): number {
    return this.height;
  }
}