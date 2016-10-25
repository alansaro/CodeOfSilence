package itesm.mx.asjr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * Created by dc on 10/24/16.
 */

public class BarraVida extends Actor {

    private NinePatchDrawable healthBarBackground;
    private NinePatchDrawable healthBar;

    public BarraVida() {
        TextureAtlas skinAtlas = new TextureAtlas(Gdx.files.internal("data/uiskin.atlas"));
        NinePatch loadingBarBackgroundPatch = new NinePatch(skinAtlas.findRegion("default-round"), 5, 5, 4, 4);
        NinePatch loadingBarPatch = new NinePatch(skinAtlas.findRegion("default-round-down"), 5, 5, 4, 4);
        healthBar = new NinePatchDrawable(loadingBarPatch);
        healthBarBackground = new NinePatchDrawable(loadingBarBackgroundPatch);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float progress = 0.4f;

        healthBarBackground.draw(batch, getX(), getY(), getWidth() * getScaleX(), getHeight() * getScaleY());
        healthBar.draw(batch, getX(), getY(), progress * getWidth() * getScaleX(), getHeight() * getScaleY());
    }


}
