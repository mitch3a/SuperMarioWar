package smw.world.hazards;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import smw.entity.Player;
import smw.gfx.Sprite;
import smw.ui.screen.GameFrame;
import smw.world.MovingCollidable;

public class BulletBillyCannon implements MovingCollidable{
  static final float baseTime = 2500f;
  static final float randomFactor = 2000f;
  float timeBetweenBullets;
  
  final List<BulletBilly> billyBullets = new LinkedList<BulletBilly>();
  
  final int x, y;
  final float velocity;
  
  float timeSinceLastBullet;
  
  public BulletBillyCannon(int x, int y, short unused, float velocity) {
    this.x = x;
    this.y = y;
    this.velocity = velocity;
    
    timeSinceLastBullet = 0;
    resetTimeBetweenBullets();
  }

  void resetTimeBetweenBullets(){
    timeBetweenBullets =  baseTime + (float)Math.random()*randomFactor;
  }
  
  @Override
  public void update(float timeDif_ms) {
    timeSinceLastBullet += timeDif_ms;
    
    if(timeSinceLastBullet > timeBetweenBullets){
      //TODO is this safe? what if there is big lag?
      timeSinceLastBullet -= timeBetweenBullets;
      
      BulletBilly bulletBilly = new BulletBilly(x, y, velocity);
      billyBullets.add(bulletBilly);
      resetTimeBetweenBullets();
    }
    
    Iterator<BulletBilly> iter = billyBullets.iterator();
    while (iter.hasNext()) {
      BulletBilly temp = iter.next();
      temp.update(timeDif_ms);
    }
  }
  
  @Override
  public float collideX(Player player, float newX) {
    Iterator<BulletBilly> iter = billyBullets.iterator();
    while (iter.hasNext()) {
      BulletBilly temp = iter.next();
      newX = temp.collideX(player, newX);
    }
    
    return newX;
  }

  @Override
  public float collideY(Player player, float newX, float newY) {
    Iterator<BulletBilly> iter = billyBullets.iterator();
    while (iter.hasNext()) {
      BulletBilly temp = iter.next();
      newX = temp.collideX(player, newY);
    }
    
    return newY;
  }
  

  @Override
  public void draw(Graphics2D g, ImageObserver io) {
    Iterator<BulletBilly> iter = billyBullets.iterator();
    while (iter.hasNext()) {
      BulletBilly temp = iter.next();
      temp.draw(g, io);
    }
  }

  @Override
  public boolean kills(Player player) {
    Iterator<BulletBilly> iter = billyBullets.iterator();
    while (iter.hasNext()) {
      BulletBilly temp = iter.next();
      if(temp.kills(player)){
        return true;
      }
    }
    
    return false;
  }

  @Override
  public boolean shouldBeRemoved() {
    return false;
  }
  
  private class BulletBilly extends AnimatedHazard{
    boolean dead = false;
    float velocity;
      
    public BulletBilly(int x, int y, float velocity) {
      super(x, y, "bulletbill.png", 32, 32, 0, 0, 0, (velocity < 0) ? 0 : 32);
      this.x = x;
      this.y = y;
      this.velocity = velocity/16f;
    }
    
    @Override
    public void update(float timeDif_ms) {
      move(velocity*timeDif_ms, 0);
    }
    
    @Override
    public float collideY(Player player, float newX, float newY) {
      if(!intersects(player) && intersects(newX, player.y, Sprite.IMAGE_WIDTH, Sprite.IMAGE_HEIGHT)){
        dead = true;
      }
      
      return newY;
    }
    
    public void move(float dx, float dy) {
      x += dx ;
      y += dy;
      
      if(y < -64){
        y = (y + dy + GameFrame.res_height + 64) % GameFrame.res_height;
      }
      calculateReturnValues();
    }
    
    @Override
    public void draw(Graphics2D g, ImageObserver io) {
      g.drawImage(getImage(), (int)x, (int)y, io);
    }

    @Override
    public boolean shouldBeRemoved() {
      return (x < -width || x > GameFrame.res_width);
    }

    @Override
    public boolean kills(Player player) {
      return intersects(player);
    }

    @Override
    public void nextFrame() {
      // TODO Auto-generated method stub  
    }
  }
}
