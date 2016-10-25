package itesm.mx.asjr;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;


/**
 * Created by dc on 10/13/16.
 */
public class PantallaMapa implements Screen
{
    // ALAN TIENE EL ANO DESTROZADO.
    public static final int ANCHO_MAPA = 6784;
    public static final int ANCHO_CAMARA = 1280;
    public static final int ALTO_CAMARA = 480;

    // Cámara
    private OrthographicCamera camara;
    private Viewport vista;

    // HUD. Los componentes en la pantalla que no se mueven
    private OrthographicCamera camaraHUD; // Cámara fija
    private StretchViewport vistaHUD;

    // Escena para HUD
    private Stage escena;

    // SpriteBatch sirve para administrar los trazos
    private SpriteBatch batch;
    private final Juego juego;  // Para regresar al menú

    //*** Para el mapa
    private TiledMap mapa;  // Información del mapa en memoria
    private OrthogonalTiledMapRenderer rendererMapa;    // Dibuja el mapa
    //***

    // Personajes animado
    private Texture texturaMario;
    private Personaje mario;
    private Enemigo bowser;

    // Pad
    private Touchpad pad;

    // Action Button
    private TextButton.TextButtonStyle textButtonStyle;
    public static TextButton actionButton;
    private BitmapFont font;

    // Para las balas
    ArrayList<Bullet> bulletList = new ArrayList<Bullet>();

    // Musica
    private Music musicaFondo;
    ///private Sound sonidoMuere;
    ///private boolean haMuertoMario=false;
    ///private Sound sonidoMoneda;

    // Screen lifecycle:
    // 1.- Show()
    // 2.- Resume()
    // 3.- Resize()
    // 4.- Render()
    // 5.- Pause()
    // 6.- Hide()
    // 7.- Dispose()

    ///_________LOOOL_____

    // Para una barra de vida
    private Texture healthBar, healthContainer;
    int vida = 32;


    public PantallaMapa(Juego juego) {  // Constructor
        this.juego = juego;
    }

    @Override
    public void show() {
        // Aquí es donde se obtienen las propiedades iniciales de nuestra pantalla.
        inicializarCamara();
        crearEscena();
        cargarMapa();   // Nuevo
        crearPad();
        inicializarVida();


        Gdx.gl.glClearColor(1,1,1,1);
    }

    private void inicializarVida() {

        int ancho = 1;
        int alto = 1;
        // Se dibuja un pixmap para el contenedor de la barra de vida.
        Pixmap pixmap1 = drawPixmap(ancho, alto, 1,0,0);
        // Se dibuja otro pixmap para la barra de vida
        Pixmap pixmap2 = drawPixmap(ancho,alto, 0,0,0);
        // Se inicializan las texturas.
        healthBar = new Texture(pixmap1);
        healthContainer = new Texture (pixmap2);

    }

    private Pixmap drawPixmap(int ancho, int alto, int r, int g, int b) {
        // Éste método dibuja el mapa de pixeles.
        Pixmap pixmap = new Pixmap (ancho, alto, Pixmap.Format.RGBA8888);
        pixmap.setColor(r,g,b,1);
        pixmap.fill();
        return pixmap;
    }

    private void createActionButtion(){

        // Crea las texturas.
        Skin skin = new Skin();
        skin.add("actionUp", new Texture ("actionUp.png"));
        skin.add("actionDown", new Texture ("actionDown.png"));

        font = new BitmapFont();

        // Características del botón.
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("actionUp");
        textButtonStyle.down = skin.getDrawable("actionDown");
        textButtonStyle.font = font;

        // Crea un botón de acción con las texturas y las características creadas.
        actionButton = new TextButton("Disparar", textButtonStyle);
        actionButton.setBounds(1130, 50, 150 ,150);

        // Agrega el objeto a la pantalla.
        escena.addActor(actionButton);

        actionButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("ActionButton", "Click sobre el botón de acción");
            }
        });

    }

    private void crearPad() {

        // Para cargar las texturas y convertirlas en Drawable
        Skin skin = new Skin();
        skin.add("touchBackground", new Texture("touchBackground.png"));
        skin.add("touchKnob", new Texture("touchKnob.png"));

        // Carcaterísticas del pad
        Touchpad.TouchpadStyle tpEstilo = new Touchpad.TouchpadStyle();
        tpEstilo.background = skin.getDrawable("touchBackground");
        tpEstilo.knob = skin.getDrawable("touchKnob");

        // Crea el pad, revisa la clase Touchpad para entender los parámetros
        pad = new Touchpad(20,tpEstilo);
        pad.setBounds(0,0,200,200); // Posición y tamaño
        pad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (mario.getEstadoMovimiento()!= Personaje.EstadoMovimiento.INICIANDO) {
                    Touchpad p = (Touchpad) actor;

                    if (p.getKnobPercentX() > 0 && p.getKnobPercentY()<.25 && p.getKnobPercentY()>-.25) {    //Derecha
                        mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_DERECHA);
                    } else if (p.getKnobPercentX() < 0 && p.getKnobPercentY()<.25 && p.getKnobPercentY()>-.25) { // Izquierda
                        mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_IZQUIERDA);
                    } else if (p.getKnobPercentY() > .25 ) { //Arriba
                        mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_ARRIBA);
                    }else if (p.getKnobPercentY() < -.1) { //Abajo
                        mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_ABAJO);
                    } else if (p.getKnobPercentX() == 0){    // Nada
                        mario.setEstadoMovimiento(Personaje.EstadoMovimiento.QUIETO);
                    }
                }

                moverEnemigo(bowser,mario);


            }
        });

        escena.addActor(pad);
        pad.setColor(1,1,1,0.4f);
        Gdx.input.setInputProcessor(escena);

    }

    private void crearEscena() {
        batch = new SpriteBatch();

        escena = new Stage();
        escena.setViewport(vistaHUD);
        crearPad();
        createActionButtion();
    }

    public void moverEnemigo(Enemigo enemy, Personaje character){

        if (character.getX() < enemy.getX() && character.getY() == enemy.getY()){
            enemy.setEstadoMovimiento(Enemigo.EstadoMovimiento.MOV_IZQUIERDA);
        }
        else if (character.getX() > enemy.getX() && character.getY() == enemy.getY()){
            enemy.setEstadoMovimiento(Enemigo.EstadoMovimiento.MOV_DERECHA);
        }

        else if (character.getY() < enemy.getY() && character.getX() == enemy.getX()){
            enemy.setEstadoMovimiento(Enemigo.EstadoMovimiento.MOV_ABAJO);
        }
        else if (character.getY() > enemy.getY() && character.getX() == enemy.getX()){
            enemy.setEstadoMovimiento(Enemigo.EstadoMovimiento.MOV_ARRIBA);
        }

    }


    private void cargarMapa() {
        AssetManager manager = new AssetManager();
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        //manager.load("MarioCompleto.tmx", TiledMap.class);
        manager.load("MarioCompleto.tmx", TiledMap.class);
        manager.load("MonitoSprite.png", Texture.class);

        // Carga música
        manager.load("Jailhouse.mp3", Music.class);
        //manager.load("audio/muereMario.mp3", Sound.class);
        //manager.load("audio/moneda.mp3", Sound.class);

        manager.finishLoading();
        mapa = manager.get("MarioCompleto.tmx");
        texturaMario = manager.get("MonitoSprite.png");

        // Crea el objeto que dibujará el mapa
        rendererMapa = new OrthogonalTiledMapRenderer(mapa,batch);
        rendererMapa.setView(camara);

        // Audio

        musicaFondo = manager.get("Jailhouse.mp3");
        //sonidoMuere = manager.get("audio/muereMario.mp3");
        //sonidoMoneda = manager.get("audio/moneda.mp3");

        musicaFondo.setLooping(true);
        musicaFondo.play();


        // Personaje y Enemigo
        mario = new Personaje(texturaMario);
        bowser = new Enemigo(texturaMario);
    }

    private void inicializarCamara() {
        camara = new OrthographicCamera(ANCHO_CAMARA, ALTO_CAMARA);
        camara.position.set(ANCHO_CAMARA/2, ALTO_CAMARA /2,0);
        camara.update();
        vista = new StretchViewport(ANCHO_CAMARA, PantallaMapa.ALTO_CAMARA,camara);

        //Cámara para HUD

        camaraHUD = new OrthographicCamera(ANCHO_CAMARA, PantallaMapa.ALTO_CAMARA);
        camaraHUD.position.set(ANCHO_CAMARA/2, PantallaMapa.ALTO_CAMARA /2, 0);
        camaraHUD.update();
        vistaHUD = new StretchViewport(ANCHO_CAMARA, PantallaMapa.ALTO_CAMARA,camaraHUD);

    }

    @Override
    public void render(float delta) {
        //El método render va a dibujar en pantalla lo que le digamos. Recibe un tiempo delta.

        // actualizar cámara (para recorrer el mundo completo)
        actualizarCamara();
        // Actualización del personaje en el mapa
        mario.actualizar(mapa);
        bowser.actualizar(mapa);
        // Borra el frame actual
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // escala la pantalla de acuerdo a la cámara y vista
        batch.setProjectionMatrix(camara.combined);
        rendererMapa.setView(camara);
        rendererMapa.render();  // Dibuja el mapa

        // Batch
        batch.begin();
        mario.render(batch);    // Dibuja el personaje
        bowser.render(batch);
        batch.draw(healthContainer, mario.getX(), mario.getY()+33, 32, 5);//Dibuja la barra de vida.
        batch.draw(healthBar, mario.getX(), mario.getY()+34 ,vida,4);
        for(Bullet bill : bulletList){
            bill.draw(batch);
        }
        batch.end();

        for(Bullet bill: bulletList){
            bill.update(Gdx.graphics.getDeltaTime());
        }

        // Dibuja el HUD
        batch.setProjectionMatrix(camaraHUD.combined);
        escena.draw();

        // Prueba si el enemigo está atacando a mario
        if (mario.getX() == bowser.getX()){
            vida--;
        }

        if(vida == 0){
            Gdx.app.log("PantallaMapa", "Has perdido maldito bastardo");
        }

        if(actionButton.getClickListener().isPressed()){
            Gdx.app.log("render" , "se está apunto de dibujar la clase Bullet.");
            bulletList.add(new Bullet ((int)mario.getX(), (int)mario.getY(),0) );
        }


    }

    // Actualiza la posición de la cámara para que el personaje esté en el centro,
    // excepto cuando está en la primera y última parte del mundo.
    private void actualizarCamara() {
        float posX = mario.getX();

        // Si está en la parte 'media'
        if (posX>=ANCHO_CAMARA/2 && posX<=ANCHO_MAPA-ANCHO_CAMARA/2) {
            // El personaje define el centro de la cámara
            camara.position.set((int)posX, camara.position.y, 0);
        } else if (posX>ANCHO_MAPA-ANCHO_CAMARA/2) {    // Si está en la última mitad
            // La cámara se queda a media pantalla antes del fin del mundo  :)
            camara.position.set(ANCHO_MAPA-ANCHO_CAMARA/2, camara.position.y, 0);
        } else if ( posX<ANCHO_CAMARA/2 ) { // La primera mitad
            camara.position.set(ANCHO_CAMARA/2, PantallaMapa.ALTO_CAMARA /2,0);
        }
        camara.update();
    }


    @Override
    public void resize(int width, int height) {
        // A éste método se llama cuando se tiene que hacer más grande o más chica la pantalla.
        vista.update(width, height);
        vistaHUD.update(width, height);
    }

    @Override
    public void pause() {
        // Salir de la aplicación o de la pantalla va a ocacionar que el juego se pause.
        // Un botón de pausa también ocacionaría lo mismo.

    }

    @Override
    public void resume() {
        // Regresar a la aplicación ocacionará que la aplicación se resuma.
        // Un botón de resumir desde el menú de pausa ocacionaría lo mismo.

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        texturaMario.dispose();
        mapa.dispose();
    }

}
