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
 * Created by dc on 10/13/16.
 */

public class Personaje
{
    public static final float VELOCIDAD_X = 4;      // Velocidad horizontal
    public static final float VELOCIDAD_Y = -4;
    private static final float V0 = 60.0f;          // Velocidad inicial al saltar
    //private static final float G = 9.81f;           // Gravedad
    //private static final float G_2 = G/2f;          // Mitad de la gravedad

    private Sprite sprite;  // Sprite cuando no se mueve (QUIETO)
    //private Sound sonidoMoneda;     // Efecto cuando colecta una moneda
    // Animación
    private Animation caminando;    // Caminando
    private Animation caminandoArriba;
    private float timerAnimacion;   // tiempo para calcular el frame

    private EstadoMovimiento estadoMovimiento=EstadoMovimiento.INICIANDO;
    //private EstadoSalto estadoSalto=EstadoSalto.CAIDA_LIBRE;
    private float tiempoSalto;  // Tiempo total en el aire
    private float yInicial; // Donde inicia el salto
    private float tiempoVuelo;  // Tiempo que lleva en el aire


    //Constructor del personaje, recibe una imagen con varios frames, (ver imagen marioSprite.png 128x64, cada tile 32x64)

    public Personaje(Texture textura) {
        //Gdx.app.log("Personaje", "Creando el personaje :)");
        //this.sonidoMoneda = sonidoMoneda;
        // Lee la textura como región
        TextureRegion texturaCompleta = new TextureRegion(textura);
        // La divide en 4 frames de 32x64 (ver MonitoSprite.png)
        TextureRegion[][] texturaPersonaje = texturaCompleta.split(32,32);
        // Crea la animación con tiempo de 0.25 segundos entre frames.
        caminando = new Animation(0.25f, texturaPersonaje[2][2],
                texturaPersonaje[2][1], texturaPersonaje[2][0]);
        // Crear una animación para que camine hacia arriba
        caminandoArriba = new Animation(0.25f, texturaPersonaje[3][2],
                texturaPersonaje[3][1], texturaPersonaje[3][0]);
        // Animación infinita
        caminando.setPlayMode(Animation.PlayMode.LOOP);
        caminandoArriba.setPlayMode(Animation.PlayMode.LOOP);
        // Inicia el timer que contará tiempo para saber qué frame se dibuja
        timerAnimacion = 0;
        // Crea el sprite con el personaje quieto (idle)
        sprite = new Sprite(texturaPersonaje[0][0]);    // QUIETO
        sprite.setPosition(300, 800);    // Posición inicial
    }

    // Dibuja el personaje
    public void render(SpriteBatch batch) {
        // Dibuja el personaje dependiendo del estadoMovimiento
        switch (estadoMovimiento) {
            case MOV_DERECHA:
            case MOV_IZQUIERDA:
                timerAnimacion += Gdx.graphics.getDeltaTime();
                TextureRegion region = caminando.getKeyFrame(timerAnimacion);
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
                System.out.println("Case mov_Arriba");
                timerAnimacion+= Gdx.graphics.getDeltaTime();
                TextureRegion region2 = caminandoArriba.getKeyFrame(timerAnimacion);
                batch.draw(region2,sprite.getX(),sprite.getY());
                break;
            case MOV_ABAJO:
                System.out.println("Case mov_Arriba");
                timerAnimacion+= Gdx.graphics.getDeltaTime();
                TextureRegion region3 = caminandoArriba.getKeyFrame(timerAnimacion);
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
                caer(mapa, VELOCIDAD_Y);
                break;

            case MOV_ARRIBA:
            case MOV_ABAJO:
                moverVertical(mapa);
                break;
        }

        /**
        switch (estadoSalto) {
            case SUBIENDO:
            case BAJANDO:
                actualizarSalto(mapa);
                break;
        }
         **/


        recolectarMonedas(mapa);

    }



    private void recolectarMonedas(TiledMap mapa) {
        // Revisar si está sobre una moneda (pies)
        TiledMapTileLayer capa = (TiledMapTileLayer)mapa.getLayers().get(0);
        int x = (int)(sprite.getX()/32);
        int y = (int)(sprite.getY()/32);
        TiledMapTileLayer.Cell celda = capa.getCell(x,y);
        if (celda!=null ) {
            Object tipo = (String)celda.getTile().getProperties().get("tipo");
            if ( "moneda".equals(tipo) ) {
                capa.setCell(x,y,capa.getCell(0,3));    // Cuadro azul en lugar de la moneda
                //sonidoMoneda.play();
            }
        }
    }


    private void moverVertical(TiledMap mapa){
        // Obtiene la primer capa del mapa (en este caso es la única)
        TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
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
            boolean abajo = leerCeldaAbajo(mapa);
            if (abajo == true) {
                Object tipo = (String) celdaAbajo.getTile().getProperties().get("tipo");
                if (!"ladrillo".equals(tipo)) {
                    celdaAbajo = null;  // Puede pasar
                }
            }
            if (abajo == false){
                //Ejecutar movimiento horizontal
                nuevaY-=4;
                sprite.setY(nuevaY);

            }
        }


    }

    // Mueve el personaje a la derecha/izquierda, prueba choques con paredes
    private void moverHorizontal(TiledMap mapa) {
        // Obtiene la primer capa del mapa (en este caso es la única)
        TiledMapTileLayer capa = (TiledMapTileLayer) mapa.getLayers().get(0);
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
/**
    private void probarCaida(TiledMap mapa) {
        boolean hayCeldaAbajo = leerCeldaAbajo(mapa);
        if (!hayCeldaAbajo) {
            estadoSalto = EstadoSalto.BAJANDO;
        }
    }
 **/

    // Avanza en su caída
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


    // Regresa true si hay alguna celda debajo del personaje
    private boolean leerCeldaAbajo(TiledMap mapa) {
        // Revisar si no hay algo que lo detenga
        TiledMapTileLayer capa = (TiledMapTileLayer)mapa.getLayers().get(0);
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

    /***
    // Ejecutar movimiento vertical, actualiza la posición en 'y'
    public void actualizarSalto(TiledMap mapa) {
        tiempoSalto += 10 * Gdx.graphics.getDeltaTime();  // Actualiza tiempo
        // Si quieren movimiento parabólico, usen esta fórmula
        //float y = V0 * tiempoSalto - G_2 * tiempoSalto * tiempoSalto;  // Desplazamiento desde que inició el salto
        if (estadoSalto==EstadoSalto.SUBIENDO && tiempoSalto>tiempoVuelo/2) { // Llegó a la altura máxima?
            // Inicia caída
            estadoSalto = EstadoSalto.BAJANDO;
        }
        if ( estadoSalto==EstadoSalto.SUBIENDO) {
            sprite.setY(sprite.getY()-VELOCIDAD_Y);   // sube
        } else if (estadoSalto==EstadoSalto.BAJANDO) { // Va hacia abajo, probar que caiga sobre un bloque
            boolean hayCeldaAbajo = leerCeldaAbajo(mapa);
            if (hayCeldaAbajo) {
                // Tocó el piso
                estadoSalto = EstadoSalto.EN_PISO;
            } else {
                sprite.setY(sprite.getY()+VELOCIDAD_Y);   // Avanza
            }
        }
    }
     **/

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

    /**
    // Inicia el salto
    public void saltar() {
        if (estadoSalto==EstadoSalto.EN_PISO) {
            tiempoSalto = 0;
            yInicial = sprite.getY();
            estadoSalto = EstadoSalto.SUBIENDO;
            tiempoVuelo = 2 * V0 / G;
        }
    }
     **/

    public enum EstadoMovimiento {
        INICIANDO,
        QUIETO,
        MOV_IZQUIERDA,
        MOV_DERECHA,
        MOV_ARRIBA,
        MOV_ABAJO
    }

/**
    public enum EstadoSalto {
        EN_PISO,
        SUBIENDO,
        BAJANDO,
        CAIDA_LIBRE // Cayó de una orilla
    }
 **/
}