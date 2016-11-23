package itesm.mx.asjr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;

/**
 * Created by AlanJoseph, dc on 06/09/2016.
 */
public class PantallaTransicionDos implements Screen
{


    private final Juego juego;
    private Stage escena;


    private final AssetManager assetManager = new AssetManager();

    private Texture texturaFondo;
    private Music YouWin;

    //private Texture texturaBtnBack;
    //private Texture texturaHazPerdido;



    public PantallaTransicionDos(Juego juego) {this.juego = juego;}

    private void cargarTexturas(){

        // Aquí se carga el fondo.
        assetManager.load("FondoTransicionUno.jpg", Texture.class);
        //assetManager.load("MenuPrincipal.png", Texture.class);
        //assetManager.load("hazperdido.png", Texture.class);
        assetManager.load("YouWin.mp3", Music.class);



        assetManager.finishLoading();

        //Calcular ancho y alto
        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        // Fondo:
        texturaFondo = assetManager.get("FondoTransicionUno.jpg");
        Image imgFondo = new Image(texturaFondo);

        YouWin = assetManager.get("YouWin.mp3");
        YouWin.play();

        // Escalar:
        float escalaX = ancho/imgFondo.getWidth();
        float escalaY = alto/imgFondo.getHeight();
        imgFondo.setScale(escalaX, escalaY);
        // Añadira la pantalla:
        //escena.addActor(imgFondo);


    }


    @Override
    public void show()
    {
        cargarTexturas();
        escena = new Stage();


        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        Gdx.input.setInputProcessor(escena);

        Image imgFondoA = new Image(texturaFondo);

        float escalaX = ancho  / imgFondoA.getWidth();
        float escalaY = alto / imgFondoA.getHeight();
        imgFondoA.setScale(escalaX, escalaY);
        escena.addActor(imgFondoA);

        /***
         // Botones
         TextureRegionDrawable trdBtnBack = new TextureRegionDrawable( new TextureRegion(texturaBtnBack));
         ImageButton btnBack = new ImageButton(trdBtnBack);
         btnBack.setPosition(ancho/2 - btnBack.getWidth()/2, 0.2f*alto);
         escena.addActor(btnBack);


         Image botonMenuPrincipal = new Image(texturaHazPerdido);
         botonMenuPrincipal.setPosition(ancho/2-botonMenuPrincipal.getWidth()/2, 0.4f*alto);
         escena.addActor(botonMenuPrincipal);
         **/


        // Para retrasar la entrada al nivel dos:
        float delay = 3; // seconds
        Timer timer = new Timer();
        Timer.schedule(new Timer.Task(){
            @Override
            public void run(){
                juego.setScreen(new PantallaNivelDos(juego));
            }
        }, delay);


    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0,1,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        escena.draw();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

        dispose();

    }

    @Override
    public void dispose() {
        texturaFondo.dispose();
        escena.dispose();
        //texturaBtnBack.dispose();
        //texturaHazPerdido.dispose();
    }
}
