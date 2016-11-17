package itesm.mx.asjr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by AlanJoseph on 06/09/2016.
 */
public class PantallaOpciones implements Screen
{

    private final Juego juego;
    private Stage escena;


    private final AssetManager assetManager = new AssetManager();

    private Texture texturaFondoO;
    private Texture texturaBtnBack;
    private Texture texturaBtnMusicOff;



    public PantallaOpciones(Juego juego) {
        this.juego = juego;
    }

    private void cargarTexturas(){

        assetManager.load("fondoOpciones.png", Texture.class);
        assetManager.load("back.png", Texture.class);
        assetManager.load("nota_boton.png", Texture.class);


        assetManager.finishLoading();


        texturaFondoO = assetManager.get("fondoOpciones.png");
        texturaBtnBack = assetManager.get("back.png");
        texturaBtnMusicOff = assetManager.get("nota_boton.png");

    }


    @Override
    public void show()
    {
        cargarTexturas();
        escena = new Stage();


        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        Gdx.input.setInputProcessor(escena);

        Image imgFondoO = new Image(texturaFondoO);

        float escalaX = ancho  / imgFondoO.getWidth();
        float escalaY = alto / imgFondoO.getHeight();
        imgFondoO.setScale(escalaX, escalaY);
        escena.addActor(imgFondoO);


        TextureRegionDrawable trdBtnBack = new TextureRegionDrawable( new TextureRegion(texturaBtnBack));
        ImageButton btnBack = new ImageButton(trdBtnBack);
        escena.addActor(btnBack);


        // Para poder apagar la música del juego:
        TextureRegionDrawable trdBtnMusicOff = new TextureRegionDrawable( new TextureRegion(texturaBtnMusicOff));
        ImageButton btnMusicOff = new ImageButton(trdBtnMusicOff);
        btnMusicOff.setBounds(915,58, 129,129);
        escena.addActor(btnMusicOff);


        // Para apagar la música durante el juego.
        btnMusicOff.addListener( new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Configuración", "no quiere musica");
                // Apagar la música del juego.
                    PantallaNivelUno.quiereMusica = false;
                    PantallaNivelDos.quiereMusica = false;
                    PantallaNivelTres.quiereMusica = false;
            }
        });


        btnBack.addListener( new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Regresar al menu pricipal
                juego.setScreen( new PantallaPrincipal(juego) );
            }
        });





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
        texturaFondoO.dispose();
        escena.dispose();
        texturaBtnBack.dispose();

    }
}
