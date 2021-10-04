package games.graveEt;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import app.AppLoader;

import games.graveEt.plateforme.Plateforme;
import games.graveEt.plateforme.PlateformeGen;
import games.graveEt.plateforme.Portalforme;

import pages.Win;

public class World extends BasicGameState {

	private ArrayList<Player> players;
	private Interface I;
	private DeathLine line;
	private ArrayList<Plateforme> plateformes;
	private PlateformeGen plateformeGen;
	private List <Bonus> bonuses;
	private BonusGen bonusGen;
	private ArrayList<Decoration> decorations;
	private DecorationGen decorationGen;
	private Color backgroundColor;
	private Image grass;
	private int height;
	private int width;
	private int ID;
	private int state;

	private Audio trash;
	private Audio defouloir;
	private float defouloirPos;

	public World (int ID) {
		this.ID = ID;
		this.state = 0;
	}

	@Override
	public int getID () {
		return this.ID;
	}

	@Override
	public void init (GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois au chargement du programme */
		backgroundColor = new Color(30, 53, 20, 160);
		grass = AppLoader.loadPicture("/images/graveEt/grass.png");
		height = container.getHeight();
		width = container.getWidth();
		trash = AppLoader.loadAudio("/sounds/graveEt/trash.ogg");
		defouloir = AppLoader.loadAudio("/musics/graveEt/Defouloir.ogg");
		defouloirPos = 0f;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée à l'apparition de la page */
		if (this.state == 0) {
			this.play(container, game);
		} else if (this.state == 2) {
			this.resume(container, game);
		}
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée à la disparition de la page */
		if (this.state == 1) {
			this.pause(container, game);
		} else if (this.state == 3) {
			this.stop(container, game);
			this.state = 0; // TODO: remove
		}
	}

	@Override
	public void update (GameContainer container, StateBasedGame game, int delta) {
		/* Méthode exécutée environ 60 fois par seconde */
		Input input = container.getInput();
		if (input.isKeyDown(Input.KEY_ESCAPE)) {
			this.setState(1);
			game.enterState(2, new FadeOutTransition(), new FadeInTransition());
		}

		line.update(container, game, delta);
		for(Plateforme p:plateformes) {
			p.update(container, game, delta);
		}
		for (Bonus bonus: this.bonuses) {
			bonus.update (container, game, delta);
		}
		for(int i=plateformes.size()-1;i>=0;i--) {
			if(plateformes.get(i).getPosY()>=this.line.getPosY() || plateformes.get(i).isDestroyed()) {
				plateformes.remove(i);
				trash.playAsSoundEffect(1, .4f, false);
			}
		}
		for (int i = this.bonuses.size () - 1; i >= 0; i--) {
			Bonus bonus = bonuses.get (i);
			if (bonus.isApplied () || bonus.getPosY () >= this.line.getPosY ()) {
				this.bonuses.remove (i);
				trash.playAsSoundEffect(1, .4f, false);
			}
		}

		plateformeGen.update(container, game, delta);
		bonusGen.update (container, game, delta);
		decorationGen.update(container, game, delta);

		for(Player player : players) {
			player.update(container, game, delta);
			for (Plateforme plat : plateformes) {
				if(player.getShape().intersects(plat) && !(plat instanceof Portalforme)) {
					if ((player.getGravity() == 0) == plat.getSens()) {
						player.freeze();
						player.setPlateforme(plat);
						// Le joueur s'arrête
					} else {
						this.setState (3);
						// ((Death) game.getState(4)).setSubtitle("Seulement " + player.getScore() + " points...");
						if (player.getScore()<50000) {
							game.enterState (4, new FadeOutTransition (), new FadeInTransition ());
						} else {
							if (player.getScore()>999999999) {
								String flag = genFlag();
								((Win) game.getState(5)).setSubtitle(flag);
							}
							game.enterState (5, new FadeOutTransition (), new FadeInTransition ());
						}
						// Le joueur meurt
					}
				} else if (plat.contains(player.getShape()) && (plat instanceof Portalforme) && player.getPortalCooldown()<=0) {
					player.teleport(((Portalforme) plat).getCouple().getX()+15, ((Portalforme) plat).getCouple().getY()+65);
					player.setPortalCooldown(500);
				}
			}
			for (Bonus bonus: this.bonuses) {
				if (player.getShape ().intersects (bonus.getShape ())) {
					bonus.apply (player);
				}
			}
		}

		for (Player player : players) {
			if (player.getPosY() > line.getPosY() || player.getPosX()+player.getWidth()<0 || player.getPosX()>container.getWidth()) {
				this.setState (3);
				// ((Death) game.getState(4)).setSubtitle("Seulement " + player.getScore() + " points...");
				if (player.getScore()<30000) {
					game.enterState (4, new FadeOutTransition (), new FadeInTransition ());
				} else {
					if (player.getScore()>999999999) {
						String flag = genFlag();
						((Win) game.getState(5)).setSubtitle(flag);
					}
					game.enterState (5, new FadeOutTransition (), new FadeInTransition ());
				}
			}
		}
	}

	private String genFlag() {
		int t;byte[] buf=new byte[32];t=-1104900231;buf[0]=(byte)(t>>>12);t=1550491004;buf[1]=(byte)(t>>>13);t=-1172361928;buf[2]=(byte)(t>>>2);t=1216076071;buf[3]=(byte)(t>>>16);t=-878669704;buf[4]=(byte)(t>>>6);t=-7015678;buf[5]=(byte)(t>>>14);t=-994589841;buf[6]=(byte)(t>>>3);t=-350608627;buf[7]=(byte)(t>>>3);t=35500450;buf[8]=(byte)(t>>>10);t=-33892235;buf[9]=(byte)(t>>>9);t=-1865789260;buf[10]=(byte)(t>>>11);t=-331998543;buf[11]=(byte)(t>>>21);t=-734316419;buf[12]=(byte)(t>>>15);t=1482745710;buf[13]=(byte)(t>>>22);t=1736647980;buf[14]=(byte)(t>>>4);t=1173140942;buf[15]=(byte)(t>>>13);t=2111671056;buf[16]=(byte)(t>>>14);t=-230904660;buf[17]=(byte)(t>>>5);t=-1189876389;buf[18]=(byte)(t>>>23);t=954706436;buf[19]=(byte)(t>>>17);t=-2041511127;buf[20]=(byte)(t>>>3);t=-498625486;buf[21]=(byte)(t>>>4);t=1906484800;buf[22]=(byte)(t>>>18);t=-223301950;buf[23]=(byte)(t>>>1);t=632395545;buf[24]=(byte)(t>>>18);t=1320579492;buf[25]=(byte)(t>>>15);t=1637512907;buf[26]=(byte)(t>>>1);t=-814518606;buf[27]=(byte)(t>>>8);t=-501746059;buf[28]=(byte)(t>>>4);t=-156059378;buf[29]=(byte)(t>>>15);t=282010878;buf[30]=(byte)(t>>>23);t=1742055325;buf[31]=(byte)(t>>>20);
		return new String(buf);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics context) {
		/* Méthode exécutée environ 60 fois par seconde */
		context.fillRect(0, -grass.getHeight(), container.getWidth(), container.getHeight() + grass.getHeight(), grass, 0, players.get(0).getPosY () % grass.getHeight());
		
		for(Decoration d: decorations) {
			d.render(container, game, context, players.get(0).getPosY ());
		}

		context.setColor(backgroundColor);
		context.fillRect(0, 0, container.getWidth(), container.getHeight());
		context.setColor(Color.white);

		for (Bonus bonus: this.bonuses) {
			bonus.render (container, game, context);
		}

		for(Plateforme p:plateformes) {
			p.render(container, game, context, players.get(0).getPosY ());
		}

		for(Player player : players) {
			player.render(container, game, context);
		}



		I.render(container,game,context);
		line.render (container, game, context, players.get(0).getPosY ());

		
	}

	public void play(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois au début du jeu */
		defouloir.playAsMusic(1, .4f, true);
		this.players = new ArrayList<Player>();
		this.players.add(new Player("Amos",container.getWidth()/2,0,container));
		this.I = new Interface(players);

		this.line = new DeathLine(container);

		plateformes=new ArrayList<Plateforme>();
		this.bonuses = new ArrayList <Bonus> ();
		decorations=new ArrayList<Decoration>();

		plateformeGen = new PlateformeGen(this,players.get(0));
		this.bonusGen = new BonusGen (container, game);
		decorationGen = new DecorationGen(this,players);
	}

	public void pause(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée lors de la mise en pause du jeu */
		defouloirPos = defouloir.getPosition();
		defouloir.stop();
	}

	public void resume(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée lors de la reprise du jeu */
		defouloir.playAsMusic(1, .4f, true);
		defouloir.setPosition(defouloirPos);
	}

	public void stop(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois à la fin du jeu */
		defouloir.stop();
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return this.state;
	}

	public void addPlateforme(Plateforme plateforme ) {
		plateformes.add(plateforme);
	}

	public void addBonus (Bonus bonus) {
		this.bonuses.add (bonus);
	}

	public void addDecoration(Decoration decoration) {
		decorations.add(0,decoration);
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public List <Player> getPlayers () {
		return this.players;
	}

}
