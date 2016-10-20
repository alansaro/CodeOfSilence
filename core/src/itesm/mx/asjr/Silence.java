package itesm.mx.asjr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Silence implements Screen
{

	private final Juego juego;
	//Escena en la pantala:
	private Stage escena;

	//Textura para la imagen de fondo:
	private Texture texturaFondo;
	// Textura para el titulo
	private	Texture texturaTitulo;

	//Textura de los botones
	private Texture texturaBtnJugar;
	private Texture texturaBtnOpciones;
	private Texture texturaBtnAcercaDe;



	//Administra la carga de assets:
	private final AssetManager assetManager = new AssetManager();

	public Silence(Juego juego) {
		this.juego = juego;
	}


	public void create ()
	{
		//Se ejecuta al cargar la pantalla
		cargarTexturas();

		escena = new Stage();

		// Habilitar manejo de eventos
		Gdx.input.setInputProcessor(escena);

		//Calcular ancho y alto
		float ancho = Gdx.graphics.getWidth();
		float alto = Gdx.graphics.getHeight();

		//Fondo
		Image imgFondo = new Image(texturaFondo);
		//Escalar
		float escalaX = ancho  / imgFondo.getWidth();
		float escalaY = alto / imgFondo.getHeight();
		imgFondo.setScale(escalaX, escalaY);
		escena.addActor(imgFondo);

		// Agregar Botones
		//Jugar
		TextureRegionDrawable trBtnJugar = new TextureRegionDrawable( new TextureRegion(texturaBtnJugar) );
		ImageButton btnJugar = new ImageButton( trBtnJugar );
		btnJugar.setPosition(ancho/2 - btnJugar.getWidth()/2, 0.6f*alto);
		escena.addActor(btnJugar);
		// Opciones
		TextureRegionDrawable trBtnOpciones = new TextureRegionDrawable( new TextureRegion(texturaBtnOpciones) );
		ImageButton btnOpciones = new ImageButton( trBtnOpciones );
		btnOpciones.setPosition(ancho/2 - btnOpciones.getWidth()/2, 0.4f*alto);
		escena.addActor(btnOpciones);
		// Acerca de
		TextureRegionDrawable trBtnAcercaDe = new TextureRegionDrawable( new TextureRegion(texturaBtnAcercaDe) );
		ImageButton btnAcercaDe = new ImageButton( trBtnAcercaDe );
		btnAcercaDe.setPosition(ancho/2 - btnAcercaDe.getWidth()/2, 0.2f*alto);
		escena.addActor(btnAcercaDe);

		// Agregar Titulo
		Image imgTitulo = new Image(texturaTitulo);
		imgTitulo.setPosition(ancho/2 - imgTitulo.getWidth()/2, 0.8f*alto);
		escena.addActor(imgTitulo);

		//Registrar Listener para atender eventos
		btnJugar.addListener( new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("cliked", "Tap Sobre el boton jugar");
				juego.setScreen(new PantallaMapa(juego));
			}
		});

		btnOpciones.addListener( new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("cliked", "Tap Sobre el boton opciones");
				juego.setScreen(new PantallaOpciones(juego));
			}
		});

		btnAcercaDe.addListener( new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log("cliked", "Tap Sobre el boton Acerca de...");
				//Cambiar pantalla
				juego.setScreen(new PantallaAcercaDe(juego));


			}
		});


	}

	private void cargarTexturas()
	{
		// Textura de fondo
		assetManager.load("fondo.jpg", Texture.class);
		// Textura de boton jugar
		assetManager.load("jugar2.png", Texture.class);
		// Textura de boton opciones
		assetManager.load("opciones2.png", Texture.class);
		// Textura de boton Acerca de
		assetManager.load("acerca_de2.png", Texture.class);
		// Textura de Titulo
		assetManager.load("titulo2.png", Texture.class);
		//Musica
		assetManager.load("burialatsea.mp3", Music.class);

		//Se bloquea hasta cargar los recursos.
		assetManager.finishLoading();

		//Cuando termina, leemos las texturas.
		texturaFondo = assetManager.get("fondo.jpg");
		texturaBtnJugar = assetManager.get("jugar2.png");
		texturaBtnOpciones = assetManager.get("opciones2.png");
		texturaBtnAcercaDe = assetManager.get("acerca_de2.png");
		texturaTitulo = assetManager.get("titulo2.png");


	}


	public void render ()
	{
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		escena.draw();

	}

	@Override
	public void show() {
		//Equivale a create
		create();

	}

	@Override
	public void render(float delta) {
		// Equivale a render()
		render();
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
	public void dispose () {
		//Liberar la memoria utilizada por los recursos.
		texturaFondo.dispose();
		texturaBtnAcercaDe.dispose();
		texturaTitulo.dispose();
		texturaBtnOpciones.dispose();
		texturaBtnJugar.dispose();
		escena.dispose();

	}

}
