package itesm.mx.asjr;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
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

/**
 * Created by apple on 26/10/16.
 */

public class PantallaGanar implements Screen {


    private final Juego juego;
    private Stage escena;


    private final AssetManager assetManager = new AssetManager();

    private Texture texturaFondoA;
    private Texture texturaBtnMenu;
    private Texture texturaHazPerdido;


    public PantallaGanar(Juego juego) {
        this.juego = juego;
    }

    private void cargarTexturas() {

        assetManager.load("FondoHazPerdido.png", Texture.class);
        assetManager.load("MenuPrincipal.png", Texture.class);
        assetManager.load("winletrero.png", Texture.class);


        assetManager.finishLoading();


        texturaFondoA = assetManager.get("FondoHazPerdido.png");
        texturaBtnMenu = assetManager.get("MenuPrincipal.png");
        texturaHazPerdido = assetManager.get("winletrero.png");

    }


    @Override
    public void show() {
        cargarTexturas();
        escena = new Stage();


        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        Gdx.input.setInputProcessor(escena);

        Image imgFondoA = new Image(texturaFondoA);

        float escalaX = ancho / imgFondoA.getWidth();
        float escalaY = alto / imgFondoA.getHeight();
        imgFondoA.setScale(escalaX, escalaY);
        escena.addActor(imgFondoA);


        TextureRegionDrawable trdBtnBack = new TextureRegionDrawable(new TextureRegion(texturaBtnMenu));
        ImageButton btnBack = new ImageButton(trdBtnBack);
        btnBack.setPosition(ancho / 2 - btnBack.getWidth() / 2, 0.2f * alto);
        escena.addActor(btnBack);


        Image botonMenuPrincipal = new Image(texturaHazPerdido);
        botonMenuPrincipal.setPosition(ancho / 2 - botonMenuPrincipal.getWidth() / 2, 0.4f * alto);
        escena.addActor(botonMenuPrincipal);


        btnBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Regresar al menu pricipal
                juego.setScreen(new PantallaPrincipal(juego));
            }
        });

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
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
        texturaFondoA.dispose();
        escena.dispose();
        texturaBtnMenu.dispose();
        texturaHazPerdido.dispose();
    }
}
