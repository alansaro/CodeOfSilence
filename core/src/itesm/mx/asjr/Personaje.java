package itesm.mx.asjr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Created by dc on 10/13/16.
 */

public class Personaje
{
    public static final float VELOCIDAD_X = 4;      // Velocidad horizontal
    public static final float VELOCIDAD_Y = -4;

    private Sprite sprite;  // Sprite cuando no se mueve (QUIETO)

    // Animación
    private Animation caminando;    // Caminando
    private Animation caminandoArriba;
    private Animation caminandoAbajo;
    private float timerAnimacion;   // tiempo para calcular el frame

    private EstadoMovimiento estadoMovimiento=EstadoMovimiento.QUIETO;
    private float tiempoSalto;  // Tiempo total en el aire
    private float yInicial; // Donde inicia el salto
    private float tiempoVuelo;  // Tiempo que lleva en el aire


    // Para disparar
    Texture texturaBala;

    //Constructor del personaje, recibe una imagen con varios frames, (ver imagen marioSprite.png 128x64, cada tile 32x64)
    public Personaje(Texture textura) {

        // Lee la textura como región ______________________________________________________________
        TextureRegion texturaCompleta = new TextureRegion(textura);
        // La divide en 4 frames de 32x64 (ver MonitoSprite.png)
        TextureRegion[][] texturaPersonaje = texturaCompleta.split(32,32);

        // Animaciones______________________________________________________________________________
        // Una animacion es un conjunto de TextureRegions que representan una animación.
        // A cada elemento de la animación se le llama un keyFrame, muchos KeyFrames forman una
        // animación.
        // Crea la animación con tiempo de 0.25 segundos entre frames.
        caminando = new Animation(0.05f,
                texturaPersonaje[0][0], texturaPersonaje[0][1], texturaPersonaje[0][2],texturaPersonaje[0][3],
                texturaPersonaje[0][4],texturaPersonaje[0][5],texturaPersonaje[0][6],texturaPersonaje[0][5],texturaPersonaje[0][4],texturaPersonaje[0][3]);
        // Crear una animación para que camine hacia arriba
        caminandoArriba = new Animation(0.05f,
                texturaPersonaje[1][0], texturaPersonaje[1][1], texturaPersonaje[1][2],texturaPersonaje[1][3],
                texturaPersonaje[1][4],texturaPersonaje[1][5],texturaPersonaje[1][6],texturaPersonaje[1][5],texturaPersonaje[1][4],
                texturaPersonaje[1][3]);
        // Crear una animación para que camine hacia abajo
        caminandoAbajo = new Animation(0.05f,
                texturaPersonaje[2][0], texturaPersonaje[2][1], texturaPersonaje[2][2],texturaPersonaje[2][3],
                texturaPersonaje[2][4],texturaPersonaje[2][5],texturaPersonaje[2][6],texturaPersonaje[2][5],texturaPersonaje[2][4],
                texturaPersonaje[2][3]);

        // Animación infinita_______________________________________________________________________
        caminando.setPlayMode(Animation.PlayMode.LOOP);
        caminandoArriba.setPlayMode(Animation.PlayMode.LOOP);
        caminandoAbajo.setPlayMode(Animation.PlayMode.LOOP);
        // Inicia el timer que contará tiempo para saber qué frame se dibuja
        timerAnimacion = 0;


        // Crea el sprite con el personaje quieto (idle)
        sprite = new Sprite(texturaPersonaje[2][3]);    // QUIETO
        sprite.setPosition(312, 72);    // Posición inicial
    }

    public void setPosition(int x, int y){
        sprite.setPosition(x, y);
    }


    // Dibuja el personaje__________________________________________________________________________
    public void render(SpriteBatch batch) {
        //El método render hace que se genere la imagen del personaje,
        //en éste caso el método lo dibuja dependiendo del estadoMovimiento en el que se encuentra.
        //DeltaTime es el tiempo en segundos desde el último render.

        switch (estadoMovimiento) {
            case MOV_DERECHA:
            case MOV_IZQUIERDA: // Si se mueve a la izquierda..
                timerAnimacion += Gdx.graphics.getDeltaTime(); // Toma el tiempo en el que se ha estado moviendo
                TextureRegion region = caminando.getKeyFrame(timerAnimacion); // Con ese tiempo obtiene el siguiente KeyFrame.
                if (estadoMovimiento==EstadoMovimiento.MOV_IZQUIERDA) {
                    if (!region.isFlipX()) {
                        region.flip(true,false);
                    }
                } else {
                    if (region.isFlipX()) {
                        region.flip(true,false);
                    }
                }
                batch.draw(region,sprite.getX(),sprite.getY());
                break;
            case MOV_ARRIBA:
                timerAnimacion+= Gdx.graphics.getDeltaTime();
                TextureRegion region2 = caminandoArriba.getKeyFrame(timerAnimacion);
                batch.draw(region2,sprite.getX(),sprite.getY());
                break;
            case MOV_ABAJO:
                timerAnimacion+= Gdx.graphics.getDeltaTime();
                TextureRegion region3 = caminandoAbajo.getKeyFrame(timerAnimacion);
                batch.draw(region3,sprite.getX(),sprite.getY());
                break;
            case QUIETO:
            case INICIANDO:
                sprite.draw(batch); // Dibuja el sprite
                break;
        }

    }

    // Actualiza el sprite, de acuerdo al estadoMovimiento y estadoSalto
    public void actualizar(TiledMap mapa) {
        switch (estadoMovimiento) {
            case MOV_DERECHA:
            case MOV_IZQUIERDA:
                moverHorizontal(mapa);
                break;

            case INICIANDO:

                break;

            case MOV_ARRIBA:
            case MOV_ABAJO:
                moverVertical(mapa);
                break;
        }

        recolectarVida(mapa);
        recolectarMorra(mapa);

    }


    private void recolectarVida(TiledMap mapa) {
        // Revisar si está sobre una moneda (pies)
        TiledMapTileLayer capa = (TiledMapTileLayer)mapa.getLayers().get(1);
        int x = (int)(sprite.getX()/32);
        int y = (int)(sprite.getY()/32);
        TiledMapTileLayer.Cell celda = capa.getCell(x,y);
        if (celda!=null ) {
            Object tipo = (String) celda.getTile().getProperties().get("tipo");
            if ("vida".equals(tipo)) {
                capa.setCell(x, y, capa.getCell(0, 3));
                PantallaNivelUno.agarrandoVida=true;
                PantallaNivelDos.agarrandoVida=true;
                PantallaNivelTres.agarrandoVida=true;
             }
        }
    }

    private void recolectarMorra(TiledMap mapa) {
        // Revisar si está sobre una moneda (pies)
        TiledMapTileLayer capa = (TiledMapTileLayer)mapa.getLayers().get(1);
        int x = (int)(sprite.getX()/32);
        int y = (int)(sprite.getY()/32);
        TiledMapTileLayer.Cell celda = capa.getCell(x,y);
        if (celda!=null ) {
            Object tipo = (String) celda.getTile().getProperties().get("tipo");
            if ("morra".equals(tipo)) {
                capa.setCell(x, y, capa.getCell(0, 3));
                PantallaNivelTres.GANAR = true;
            }
        }
    }


    private void moverVertical(TiledMap mapa){
        // Obtiene la primer capa del mapa (en este caso es la única)
        TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
        TiledMapTileLayer capa1 = (TiledMapTileLayer) mapa.getLayers().get(1);
        //Ejecutar el movimiento vertical
        float nuevaY = sprite.getY();
        // ¿Quiere ir hacia arriba?
        if (estadoMovimiento == EstadoMovimiento.MOV_ARRIBA){
            //Obtener el bloque de arriba. Asigna null si puede pasar.
            int y = (int) ((sprite.getY()+32)/ 32);
            int x = (int) (sprite.getX()/ 32);

            TiledMapTileLayer.Cell celdaArriba = capa1.getCell(x,y);
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
            int x = (int) ((sprite.getX())/ 32);
            int y = (int) ((sprite.getY()) / 32);
            TiledMapTileLayer.Cell celdaAbajo = capa1.getCell(x,y);
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

    // Mueve el personaje a la derecha/izquierda, prueba choques con paredes
    private void moverHorizontal(TiledMap mapa) {

        // Obtiene las capas del mapa:
        TiledMapTileLayer capa1 = (TiledMapTileLayer) mapa.getLayers().get(1);
        // Ejecutar movimiento horizontal
        float nuevaX = sprite.getX();
        // ¿Quiere ir a la Derecha?
        if ( estadoMovimiento==EstadoMovimiento.MOV_DERECHA) {
            // Obtiene el bloque del lado derecho. Asigna null si puede pasar.
            int x = (int) ((sprite.getX() + 32) / 32);   // Convierte coordenadas del mundo en coordenadas del mapa
            int y = (int) (sprite.getY() / 32);

            TiledMapTileLayer.Cell celdaDerecha = capa1.getCell(x, y);

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
                if (nuevaX <= PantallaNivelUno.ANCHO_MAPA - sprite.getWidth()) {
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
            TiledMapTileLayer.Cell celdaIzquierda = capa1.getCell(xIzq, y);
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
        MOV_ABAJO,
    }


}
