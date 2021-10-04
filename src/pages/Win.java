package pages;

import java.util.Arrays;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import app.AppMenu;
import app.elements.MenuItem;

public class Win extends AppMenu {

	public Win(int ID) {
		super(ID);
	}

	public void init(GameContainer container, StateBasedGame game) {
		super.initSize(container, game, 600, 400);
		super.init(container, game);
		this.setTitle("Bien joué !");
		this.setSubtitle(genFlag());
		this.setMenu(Arrays.asList(new MenuItem[] {
			new MenuItem("Rejouer") {
				public void itemSelected() {
					game.enterState(6, new FadeOutTransition(), new FadeInTransition());
				}
			},
			new MenuItem("Quitter") {
				public void itemSelected() {
					game.enterState(1, new FadeOutTransition(), new FadeInTransition());
				}
			}
		}));
	}

	private String genFlag() {
		int t;byte[] buf=new byte[25];t=1967268520;buf[0]=(byte)(t>>>20);t=1111186602;buf[1]=(byte)(t>>>19);t=17842589;buf[2]=(byte)(t>>>18);t=-702843658;buf[3]=(byte)(t>>>1);t=1922835392;buf[4]=(byte)(t>>>7);t=-955921470;buf[5]=(byte)(t>>>10);t=-2106389567;buf[6]=(byte)(t>>>11);t=103526434;buf[7]=(byte)(t>>>11);t=1698451979;buf[8]=(byte)(t>>>24);t=1961432991;buf[9]=(byte)(t>>>10);t=-283465451;buf[10]=(byte)(t>>>9);t=759486067;buf[11]=(byte)(t>>>24);t=-1410218761;buf[12]=(byte)(t>>>23);t=2073229049;buf[13]=(byte)(t>>>4);t=-1352356416;buf[14]=(byte)(t>>>5);t=1064688422;buf[15]=(byte)(t>>>3);t=1798115334;buf[16]=(byte)(t>>>19);t=1849397946;buf[17]=(byte)(t>>>11);t=13497661;buf[18]=(byte)(t>>>17);t=-840835726;buf[19]=(byte)(t>>>6);t=-240723636;buf[20]=(byte)(t>>>9);t=-1495989888;buf[21]=(byte)(t>>>12);t=-1160769571;buf[22]=(byte)(t>>>23);t=-166858311;buf[23]=(byte)(t>>>2);t=2016927732;buf[24]=(byte)(t>>>12);
		return new String(buf);
	}

}
