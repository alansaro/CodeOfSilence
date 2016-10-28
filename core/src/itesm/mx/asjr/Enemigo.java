package itesm.mx.asjr;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Created by AlanJoseph on 21/10/2016.
 */

public class Enemigo
{

    public static final float VELOCIDAD_X = 4;      // Velocidad horizontal
    public static final float VELOCIDAD_Y = -4;
    private static final float V0 = 60.0f;          // Velocidad inicial al saltar

    // Sprite cuando no se mueve (QUIETO)
    private Sprite sprite;

    // Animación
    private Animation caminando;    // Caminando
    private Animation caminandoArriba;
    private Animation caminandoAbajo;
    private float timerAnimacion;   // tiempo para calcular el frame

    private EstadoMovimiento estadoMovimiento=EstadoMovimiento.INICIANDO;
    //private EstadoSalto estadoSalto=EstadoSalto.CAIDA_LIBRE;
    private float tiempoSalto;  // Tiempo total en el aire
    private float yInicial; // Donde inicia el salto
    private float tiempoVuelo;  // Tiempo que lleva en el aire

    public Enemigo (Texture textura) {

        // Lee la textura como región
        TextureRegion texturaCompleta = new TextureRegion(textura);
        // La divide en 4 frames de 32x64 (ver MonitoSprite.png)
        TextureRegion[][] texturaEnemigo = texturaCompleta.split(32, 32);

        // animación.
        // Crea la animación con tiempo de 0.25 segundos entre frames.
        caminando = new Animation(0.25f, texturaEnemigo[0][2],
                texturaEnemigo[0][1], texturaEnemigo[0][0]);
        // Crear una animación para que camine hacia arriba
        caminandoArriba = new Animation(0.25f, texturaEnemigo[1][2],
                texturaEnemigo[1][1], texturaEnemigo[1][0]);
        // Crear una animación para que camine hacia abajo
        caminandoAbajo = new Animation(0.25f, texturaEnemigo[2][2],
                texturaEnemigo[2][1], texturaEnemigo[2][0]);

        // Animación infinita
        caminando.setPlayMode(Animation.PlayMode.LOOP);
        caminandoArriba.setPlayMode(Animation.PlayMode.LOOP);
        // Inicia el timer que contará tiempo para saber qué frame se dibuja
        timerAnimacion = 3;

        // Crea el sprite con el enemigo quieto (idle)
        sprite = new Sprite(texturaEnemigo[0][3]);    // QUIETO
            // Posición inicial
         }

        public void setPosition(int x, int y){
            sprite.setPosition(x, y);
        }

        // Dibuja el enemigo_____________________________________________________________________________________________________________

    public void render(SpriteBatch batch) {

        /**
            El método render hace que se genere la imagen del enemigo,
            en éste caso el método lo dibuja dependiendo del estadoMovimiento en el que se encuentra.
            DeltaTime es el tiempo en segundos desde el último render.
         **/

        switch (estadoMovimiento) {
            case MOV_DERECHA:
            case MOV_IZQUIERDA: // Si se mueve a la izquierda..
                timerAnimacion += Gdx.graphics.getDeltaTime(); // Toma el tiempo en el que se ha estado moviendo
                TextureRegion region = caminando.getKeyFrame(timerAnimacion); // Con ese tiempo obtiene el siguiente KeyFrame.
                if (estadoMovimiento == EstadoMovimiento.MOV_IZQUIERDA) {
                    if (!region.isFlipX()) {
                        region.flip(true, false);
                    }
                } else {
                    if (region.isFlipX()) {
                        region.flip(true, false);
                    }
                }
                batch.draw(region, sprite.getX(), sprite.getY());
                break;
            case MOV_ARRIBA:
                timerAnimacion += Gdx.graphics.getDeltaTime();
                TextureRegion region2 = caminandoArriba.getKeyFrame(timerAnimacion);
                batch.draw(region2, sprite.getX(), sprite.getY());
                break;
            case MOV_ABAJO:
                timerAnimacion += Gdx.graphics.getDeltaTime();
                TextureRegion region3 = caminandoAbajo.getKeyFrame(timerAnimacion);
                batch.draw(region3, sprite.getX(), sprite.getY());
                break;
            case QUIETO:
            case INICIANDO:
                sprite.draw(batch); // Dibuja el sprite
                break;

        }

    }

    public void actualizar(TiledMap mapa) {
        switch (estadoMovimiento) {
            case MOV_DERECHA:
            case MOV_IZQUIERDA:
                moverHorizontal(mapa);
                break;

            case INICIANDO:
                caer(mapa, VELOCIDAD_Y);
                break;

            case MOV_ARRIBA:
            case MOV_ABAJO:
                moverVertical(mapa);
                break;
        }

    }




    private void moverVertical(TiledMap mapa)
    {
        // Obtiene la primer capa del mapa (en este caso es la única)
        TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
        //TiledMapTileLayer capa1 = (TiledMapTileLayer) mapa.getLayers().get(1);
        //Ejecutar el movimiento vertical
        float nuevaY = sprite.getY();
        // ¿Quiere ir hacia arriba?
        if (estadoMovimiento == EstadoMovimiento.MOV_ARRIBA){
            //Obtener el bloque de arriba. Asigna null si puede pasar.
            int y = (int) ((sprite.getY() +32) / 32);
            int x = (int) (sprite.getX()/ 32);
            TiledMapTileLayer.Cell celdaArriba = capa.getCell(x,y);
            if (celdaArriba != null) {
                Object tipo = (String) celdaArriba.getTile().getProperties().get("tipo");
                if (!"ladrillo".equals(tipo)) {
                    celdaArriba = null;  // Puede pasar
                }
            }
            if (celdaArriba == null){
                //Ejecutar movimiento horizontal
                nuevaY+=4;
                sprite.setY(nuevaY);

            }
        }
        if (estadoMovimiento == EstadoMovimiento.MOV_ABAJO){
            //Obtener el bloque de ABAJO. Asigna null si puede pasar.
            int x = (int) ((sprite.getX() -32)/ 32);
            int y = (int) ((sprite.getY() -32) / 32);
            TiledMapTileLayer.Cell celdaAbajo = capa.getCell(x,y);
            if (celdaAbajo!=null) {
                Object tipo = (String) celdaAbajo.getTile().getProperties().get("tipo");
                if (!"ladrillo".equals(tipo)) {
                    celdaAbajo = null;  // Puede pasar
                }
            }
            if (celdaAbajo == null){
                //Ejecutar movimiento horizontal
                nuevaY-=4;
                sprite.setY(nuevaY);

            }
        }


    }


    private void moverHorizontal(TiledMap mapa) {
        // Obtiene la primer capa del mapa (en este caso es la única)
        TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
        //TiledMapTileLayer capa1 = (TiledMapTileLayer) mapa.getLayers().get(1);
        // Ejecutar movimiento horizontal
        float nuevaX = sprite.getX();
        // ¿Quiere ir a la Derecha?
        if ( estadoMovimiento==EstadoMovimiento.MOV_DERECHA) {
            // Obtiene el bloque del lado derecho. Asigna null si puede pasar.
            int x = (int) ((sprite.getX() + 32) / 32);   // Convierte coordenadas del mundo en coordenadas del mapa
            int y = (int) (sprite.getY() / 32);
            TiledMapTileLayer.Cell celdaDerecha = capa.getCell(x, y);
            if (celdaDerecha != null) {
                Object tipo = (String) celdaDerecha.getTile().getProperties().get("tipo");
                if (!"ladrillo".equals(tipo)) {
                    celdaDerecha = null;  // Puede pasar
                }
            }
            if ( celdaDerecha==null) {
                // Ejecutar movimiento horizontal
                nuevaX += VELOCIDAD_X;
                // Prueba que no salga del mundo por la derecha
                if (nuevaX <= PantallaMapa.ANCHO_MAPA - sprite.getWidth()) {
                    sprite.setX(nuevaX);
                    //probarCaida(mapa);
                }
            }
        }
        // ¿Quiere ir a la izquierda?
        if ( estadoMovimiento==EstadoMovimiento.MOV_IZQUIERDA) {
            int xIzq = (int) ((sprite.getX()) / 32);
            int y = (int) (sprite.getY() / 32);
            // Obtiene el bloque del lado izquierdo. Asigna null si puede pasar.
            TiledMapTileLayer.Cell celdaIzquierda = capa.getCell(xIzq, y);
            if (celdaIzquierda != null) {
                Object tipo = (String) celdaIzquierda.getTile().getProperties().get("tipo");
                if (!"ladrillo".equals(tipo)) {
                    celdaIzquierda = null;  // Puede pasar
                }
            }
            if ( celdaIzquierda==null) {
                // Prueba que no salga del mundo por la izquierda
                nuevaX -= VELOCIDAD_X;
                if (nuevaX >= 0) {
                    sprite.setX(nuevaX);
                }
            }
        }
    }


    // Regresa true si hay alguna celda debajo del enemigo
    private boolean leerCeldaAbajo(TiledMap mapa) {
        // Revisar si no hay algo que lo detenga
        TiledMapTileLayer capa = (TiledMapTileLayer)mapa.getLayers().get(0);
        //TiledMapTileLayer capa1 = (TiledMapTileLayer)mapa.getLayers().get(1);
        int x = (int)((sprite.getX())/32);
        int y = (int)(sprite.getY()+VELOCIDAD_Y)/32;
        TiledMapTileLayer.Cell celdaAbajo = capa.getCell(x,y);
        if (celdaAbajo!=null ) {
            Object tipo = (String)celdaAbajo.getTile().getProperties().get("tipo");
            if ( !"ladrillo".equals(tipo) ) {
                celdaAbajo = null;
            }
        }
        TiledMapTileLayer.Cell celdaAbajoDerecha = capa.getCell(x+1,y);
        if (celdaAbajoDerecha!=null ) {
            Object tipo = (String)celdaAbajoDerecha.getTile().getProperties().get("tipo");
            if ( !"ladrillo".equals(tipo) ) {
                celdaAbajoDerecha = null;
            }
        }
        return celdaAbajo!=null || celdaAbajoDerecha!=null;
    }


    public void caer(TiledMap mapa, float desplazamiento) {
        // Recupera la celda inferior (regresa null si no hay)
        boolean hayCeldaAbajo = leerCeldaAbajo(mapa);

        if (!hayCeldaAbajo) { // Se puede mover
            sprite.setY(sprite.getY() + desplazamiento);
        } else {
            estadoMovimiento = EstadoMovimiento.QUIETO;
            //estadoSalto = EstadoSalto.EN_PISO;
        }
    }

    //comprobara si existe colisión entre la bala y el enemigo
    //se comprobara con el metodo contein si existe una colisión
    //en el caso que exista una se pasara al siguiente metodo


    //private boolean collision(){
      //  return Bullet.getBounds().contains(Bullet.getBounds());

    //}

    //En el momento de la colisión se desaparecera el enemigo
    //






    // Accesores para la posición
    public float getX() {
        return sprite.getX();
    }

    public float getY() {
        return sprite.getY();
    }

    // Accesor de estadoMovimiento
    public EstadoMovimiento getEstadoMovimiento() {
        return estadoMovimiento;
    }

    // Modificador de estadoMovimiento
    public void setEstadoMovimiento(EstadoMovimiento estadoMovimiento) {
        this.estadoMovimiento = estadoMovimiento;
    }



    public enum EstadoMovimiento {
        INICIANDO,
        QUIETO,
        MOV_IZQUIERDA,
        MOV_DERECHA,
        MOV_ARRIBA,
        MOV_ABAJO
    }



}
