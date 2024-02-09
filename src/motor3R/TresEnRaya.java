package motor3R;
import java.util.Random;
/** Tres en raya de un solo jugador contra la máquina */
public class TresEnRaya {
    private final char[][] tablero = new char[3][3];

    //Estos caracteres solo se usarán de forma interna, los que se mostrará al jugador se determinarán en la interfaz
    private static final char cjug = 'X';
    private static final char ccpu = 'O';
    private char dif;

    /** Crea un tablero de 3x3 vacío para jugar y determina la dificultad. Una casilla vacía se representa con el caracter '.'
     * @param dif la dificultad del juego. Puede ser fácil (por defecto), media (m) o difícil (d)
     */
    public TresEnRaya(char dif){
        for(int i=0;i<tablero.length;i++){
            for(int j=0;j<tablero.length;j++){
                tablero[i][j] = '.';
            }
        }
        this.dif=dif;
    }

    /** Getter del tablero
     * 
     * @return Devuelve el tablero en formato matriz de caracteres. Una casilla marcada por el jugador se representa con una 'X', aquellas marcadas por la máquina mediante 'O' y las vacías mediante '.'
     */
    public char[][] getTablero(){
        return tablero.clone();
    }

    /** Decide si empieza el jugador o la máquina
     * 
     * @return True: empieza el jugador; False: empieza la máquina
     * 
     */
    public boolean EmpiezaJugador(){
        int res = new Random().nextInt(2);
        if(res==0){
            return true;
        }
        else return false;
    }
    

    /** Se ejecuta un turno completo de jugador y máquina, respectivamente. Debido a que puede haber 5 resultados posibles devuelve un entero en lugar de un booleano
     * 
     * @param x Fila introducida por el jugador (empezando por 1)
     * @param y Columna introducida por el jugador (empezando por 1)
     * @return -1: Pierde el jugador; 0: Continúa el juego; 1: Gana el jugador; 2: Empate; 3: Movimiento ilegal
     */
    public int Turno(int x, int y){        
        int men;
        //Omite el turno del jugador si ya hay un empate
        if((men=checkTurno(tablero))!=0){
            return men;
        }
        x--;
        y--;
        if(x>3 || y>3 || x<0 || y<0 ||tablero[x][y]!='.'){
            return 3;
        }

        else{
            tablero[x][y]=cjug;
        }

        if((men=checkTurno(tablero))!=0){
            return men;
        }

        else return turnoMaquina();
        
    }

    /** Se ejecuta un turno de la máquina. El comportamiento de la misma estará determinado por la dificultad.
     * Coexiste con el método turnoJugador ya que puede interesar ejecutar solamente ese "medio turno" (como por ejemplo si la máquina tiene el primer turno)
     * 
     * @return Si gana la máquina devuelve -1, si el juego continúa devuelve 0, si ya ha ganado el jugador devuelve 1, si hay empate devuelve 2
    */
    public int turnoMaquina(){
        //Comprueba si ya ha ganado, perdido o empatado
        if(checkLose(tablero)){
            return -1;
        }
        if(!quedanLibres(tablero)){
            return 2;
        }

        switch (dif) {
            default:
                turnoFacil(tablero, ccpu);                
                break;
            case 'i':
                turnoMedio(tablero, ccpu); 
                break;

            case 'd':
                turnoDificil(tablero, ccpu);   
                break;           
        }
        if(checkLose(tablero)){
            return -1;
        }
        else if(!quedanLibres(tablero)){
            return 2;
        }
        else{
            return 0;
        }
    }
    

    private static void turnoFacil(char[][] tablero, char ccpu){       
        int[][] posibles = calcJugadasFacil(tablero);
        int cont=cuantasJugadas(posibles);

        //Elige jugada al azar entre las posibles y asigna el caracter de la máquina a esa casilla
        int[] jugada = posibles[new Random().nextInt(cont)];

        tablero[jugada[0]][jugada[1]]=ccpu;
    }

    private static void turnoMedio(char[][] tablero, char ccpu){        
        int[][] posibles = calcJugadasMedio(tablero);
        int cont=cuantasJugadas(posibles);
        int[] jugada = posibles[new Random().nextInt(cont)];
        tablero[jugada[0]][jugada[1]]=ccpu;
    }

    private static void turnoDificil(char[][] tablero, char ccpu){
        int[][] posibles = calcJugadasDificil(tablero);

        int cont=cuantasJugadas(posibles);
        int[] jugada;
        if(cont>0){
            jugada = posibles[new Random().nextInt(cont)];
        }   
        else{
            jugada = posibles[0];
        }             
        tablero[jugada[0]][jugada[1]]=ccpu;
    }


    //Calcula posibles jugadas
    private static int[][] calcJugadasFacil(char[][] tablero){
        int cont = 0;

        //[Jugadas máximas posibles(9)][coordenadas]
        int posibles[][] = new int[9][];
        
        for(int i=0;i<tablero.length;i++){
            for(int j=0;j<tablero[i].length;j++){
                if(tablero[i][j]=='.'){
                    posibles[cont] = new int[]{i,j};
                    cont++;
                }
            }
        }
        return posibles;
    }

    //Comprueba si en los alrededores de las casillas libres existen al menos 2 caracteres de la máquina (posibilidad de hacer raya). En caso de no haber se devuelve el array original
    private static int[][] calcJugadasMedio(char[][] tablero){
        
        int[][] posibles = calcJugadas(tablero, ccpu,cjug);

        int contpos = cuantasJugadas(posibles);

        //Si no existe jugada aplicable se aplica el cálculo de dificultad fácil
        if(contpos>0) return posibles;
        else {
            return calcJugadasFacil(tablero);
        }                      
    }

    //Comprueba si el jugador tiene alguna oportunidad de raya y de ser así trata de taparla.
    //Es básicamente el mismo código que la versión intermedia cambiando caracteres a buscar.
    //Seguramente podría reutilizar ese mismo código para mayor limpieza, pero miedo me da refactorizar este mamotreto así que ahí queda
    private static int[][] calcJugadasDificil(char[][] tablero){  

        int[][] posibles = calcJugadas(tablero, cjug,ccpu);

        int contpos = cuantasJugadas(posibles);

        if(contpos>0){
            return posibles;
        }
        //Si no existe jugada aplicable se aplica el cálculo de dificultad media
        return calcJugadasMedio(tablero);
    }

    //cuenta cuantas jugadas posibles hay (itera hasta que encuentra un nulo o llega al final del array)
    private static int cuantasJugadas(int[][] posibles){
        int cont=0;
        while(posibles[cont]!=null && cont<posibles.length-1){
            cont++;
        }
        return cont;
    }

    private static boolean quedanLibres(char[][] tablero){
        int cont = 0;

        for(int i=0;i<tablero.length;i++){
            for(int j=0;j<tablero.length;j++){
                if(tablero[i][j]=='.') cont++;
            }
        }
        
        if(cont>0) return true;

        else return false;

    }

    //Comprueba si alguien ha conseguido hacer raya
    private static boolean checkEnd(char[][] tablero, char c){
        //Comprueba diagonales. Solo tiene sentido buscar si se tiene marcado el centro
        if(tablero[1][1]==c){
            if((tablero[0][0]==c && tablero[2][2]==c)||(tablero[0][2]==c && tablero[2][0]==c)){
                return true;
            }
        }
        
        int hitsf=0;
        int hitsc=0;

        //Comprueba filas y columnas        
        for(int i=0;i<tablero.length;i++){       
            hitsc=0;
            hitsf=0;         
            for(int j=0;j<tablero[i].length;j++){                
                if(tablero[i][j]==c){
                    hitsf++;
                }
                if(tablero[j][i]==c){
                    hitsc++;
                }
            }
            if(hitsf>2||hitsc>2) break;
        }

        if(hitsf>2 || hitsc>2) return true;        
        else return false;
    }

    //Comprueba si gana el jugador
    private static boolean checkWin(char[][] tablero){
        return checkEnd(tablero, cjug);
    }

    //Comprueba si gana la máquina
    private static boolean checkLose(char[][] tablero){
        return checkEnd(tablero, ccpu);
    }

    private static int checkTurno(char[][] tablero){        
        if(checkWin(tablero)){
            return 1;
        }

        if(checkLose(tablero)){
            return -1;
        }

        else if(!quedanLibres(tablero)){
            return 2;
        }

        else{
            return 0;
        }
    }

    //Método que realiza los cálculos de las dificultades que no son fácil. pro es el caracter que cuenta para hit (ccpu para media y cjug para difícil) y opo el contrario.
    private static int[][] calcJugadas(char[][] tablero, char pro, char opo){  

        //Máximas posibilidades: 5 (se necesitan mínimo 2 marcas en el tablero para tener oportunidad de raya, por lo que el jugador tendrá, como mínimo, otras 2, quedando 5 restantes)
        int[][] posibles = new int[5][];
        int contpos = 0;


        //Se comprueban jugadas posibles que sean compatibles

        for(int i=0;i<tablero.length;i++){
            for(int j=0;j<tablero[i].length;j++){                
                boolean posible=false;
                boolean hit=false;
                //Si la casilla está libre se realiza la comprobación
                if(tablero[i][j]=='.'){
                    //Se comprueban filas                  
                    for(int x=0;x<tablero.length;x++){
                        //Si hay una marca propia cuenta como hit, si ya había un hit se cuenta como posible
                        if(tablero[i][x]==pro){
                            if(hit){
                                posible=true;
                            }
                            else hit=true;
                        }
                        //Si hay una marca opuesta se anula como posible y deja de buscar
                        else if(tablero[i][x]==opo){
                            posible=false;
                            break;
                        }
                    }
                    if(posible){
                        //Si cuenta como posible se incluye en el array de posibles jugadas y se sigue buscando
                        posibles[contpos] = new int[]{i,j};
                        contpos++;
                        continue;
                    }                   
                    //Se comprueban columnas              
                    hit=false;
                    for(int x=0;x<tablero.length;x++){
                        if(tablero[x][j]==pro){
                            if(hit){
                                posible=true;
                            }
                            else hit=true;
                        }
                        else if(tablero[x][j]==opo){
                            posible=false;
                            break;
                        }
                    }
                    if(posible){
                        posibles[contpos] = new int[]{i,j};
                        contpos++;
                        continue;
                    }

                    //Se comprueba diagonal principal
                    hit=false;
                    if((i==0 && j==0)||(i==1 && j==1)||(i==2 && j==2)){
                        for(int x=0;x<tablero.length;x++){
                            if(tablero[x][x]==pro){
                                if(hit){
                                    posible=true;
                                }
                                else hit=true;                                
                            }
                            else if(tablero[x][x]==opo){
                                posible=false;
                                break;
                            }
                        }
                        if(posible){
                            posibles[contpos] = new int[]{i,j};
                            contpos++;
                            continue;
                        }
                    }

                    //Se comprueba diagonal secundaria
                    hit=false;
                    if((i==0 && j==2)||(i==1 && j==1)||(i==2 && j==0)){
                        for(int x=0;x<tablero.length;x++){
                            if(tablero[x][tablero.length-1-x]==pro){
                                if(hit){
                                    posible=true;
                                }
                                else hit=true;                                
                            }
                            else if(tablero[x][tablero.length-1-x]==opo){
                                posible=false;
                                break;
                            }
                        }
                        if(posible){
                            posibles[contpos] = new int[]{i,j};
                            contpos++;
                            continue;
                        }
                    }
                }
            }
        }
        return posibles;
    }
    

}
