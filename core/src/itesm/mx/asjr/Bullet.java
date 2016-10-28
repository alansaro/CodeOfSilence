package itesm.mx.asjr;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by dc on 10/25/16.
 */

public class Bullet {

    Rectangle hitbox;
    float a, time;
    int speed; //Frames per second.
    Texture texturaBala;



    public Bullet(int x, int y, float angle){
        time = 2;
        speed = 2000;
        hitbox = new Rectangle(x,y,10,10);
        texturaBala = new Texture("bala.png");
        a = angle;
    }

    public Rectangle getHitbox(){
        return hitbox;
    }

    public void update(float delta) {
        hitbox.x += speed * (float)Math.cos(a)* delta;
        hitbox.y += speed * (float)Math.sin(a)* delta;
        time -= delta;
    }

    public boolean isDead(){
        if(time<0) return true;
        return false;
    }

    public void draw(SpriteBatch batch){
        batch.draw(texturaBala,hitbox.x, hitbox.y,32,32);

    }
}
