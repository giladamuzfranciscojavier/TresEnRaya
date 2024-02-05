import motor3R.TresEnRaya;
import java.util.Scanner;

public class InterfaceConsola {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        boolean end=false;
        boolean start=false;
        boolean turnoJugador=false;
        TresEnRaya juego = null;

        int f;
        int c;
        
        String input="";
        char ch;

        while(!end){
            if(!start){
                start=true;
                System.out.println("Escoge nivel de dificultad: facil (por defecto), intermedio(i), dificil(d)");
                input = sc.nextLine();
                if(input.length()>0){
                    ch = input.toLowerCase().charAt(0);
                }
                else {
                    ch='f';
                }
                juego = new TresEnRaya(ch);
                System.out.println("Nueva partida. Tablero vacío");
                mostrarTablero(juego.getTablero());
                turnoJugador=juego.EmpiezaJugador();
            }            

            int men;

            if(turnoJugador){
                System.out.println("Jugador, indica coordenadas de tu movimiento");
                System.out.print("fila: ");
                try{
                    f = Integer.parseInt(sc.nextLine());
                    System.out.print("columna: ");
                    c = Integer.parseInt(sc.nextLine());
                }
                catch(NumberFormatException e){
                    System.out.println("Error: debes introducir valores numéricos");
                    continue;
                }

                men = juego.turnoJugador(f, c);
                mostrarTablero(juego.getTablero());
            }

            else {
                turnoJugador=true;
                men=99;
            }            

            switch (men) {
                case 99:
                    System.out.println("Juega la maquina...");
                    int maq = juego.turnoMaquina();
                    mostrarTablero(juego.getTablero());
                    if(maq>0){
                        if(maq==1){
                            System.out.println("El ganador es .... La maquina");  
                        }   
                        else if(maq==2){
                            System.out.println("El ganador es .... no hay ganador, la partida queda en empate");
                        } 
                        start=false;
                        end=reinicio(sc);                 
                    }
                    break;
            
                case 0:
                    continue;
                    
                case 1:
                    System.out.println("El ganador es .... El jugador");
                    start=false;
                    end=reinicio(sc);
                    break;

                case 2:
                    System.out.println("El ganador es .... no hay ganador, la partida queda en empate");
                    start=false;
                    end=reinicio(sc);
                    break;

                case -1:
                    System.out.println("El ganador es .... La maquina");  
                    start=false;
                    end=reinicio(sc);
                    break;
                    
                default:
                    System.out.println("Las coordenadas introducidas no son válidas. Inténtalo de nuevo");
                    break;
            }

        }
        sc.close();
        System.out.println("¡Adios!");       
    }




    static void mostrarTablero(char[][] tablero){
        for(int i=0;i<tablero.length;i++){
            for(int j=0;j<tablero[i].length;j++){
                switch (tablero[i][j]) {
                    case '.':
                        System.out.print("|_|");
                        break;

                    case 'X':
                        System.out.print("|X|");
                        break;

                    case 'O':
                        System.out.print("|O|");
                        break;
                    default:
                        break;
                }
                
            }
            System.out.println();
        }
    }

    static boolean reinicio(Scanner sc){
        System.out.println("¿otra partidita?(SI/NO)");
        String input=sc.nextLine();
        if(input.length()>0 && input.toUpperCase().charAt(0)=='S'){
            return false;
        }
        else{
            return true;
        }        
    }
}
