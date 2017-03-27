import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean choixBon = false;
		int choix = -1;
		while (!choixBon) {
			try {
				Scanner sc = new Scanner(System.in);
				System.out.println("Qui Commence (0 : humain, 1 : ordinateur) ?");
				choix = sc.nextInt();
				if (choix == 0 || choix == 1) {
					choixBon = true;
				} else {
					System.out.println("Vous devez saisir un entier entre 0 et 1");
				}
			} catch (InputMismatchException e) {
				System.out.println("Vous devez saisir un entier");
			}
		}

		Etat e = new Etat();
		e.setJoueur(choix);
		Etat.FinDePartie fin;

		
		Coup c;
		do {
		
			if (e.getJoueur() == 0) {
				// humain qui joue
				System.out.println("A vous de jouer !");
				do {
					c = demanderCoup(e);
				} while (!e.jouerCoup(c));
			} else {
				// ordi qui joue
				System.out.println("Ordi de jouer");
				
				e.ordiJoueMCTS(3);
			}
			e.affichage();
			fin = e.testFin();
		} while (fin == Etat.FinDePartie.NON);
		
		if(fin == Etat.FinDePartie.NON){
			e.affichage();
		}
		
		
		if(fin == Etat.FinDePartie.ORDI_GAGNE){
			System.out.println("** L'ordinateur a gagné **");
		}else if(fin == Etat.FinDePartie.MATCHNUL){
			System.out.println(" Match nul ! ");
		}else{
			System.out.println("** BRAVO, l'ordinateur a perdu  **");
		}
		/*
		 * Etat e = new Etat(); e.affichage(); System.out.println(e.testFin());
		 */
	}
	
	static Coup demanderCoup(Etat e){

		boolean choixJ = false;
		int j = -1;
		while (!choixJ) {
			try {
				Scanner sc = new Scanner(System.in);
				System.out.println("Quelle colonne ?");
				j = sc.nextInt();
				if (j >= 0 && j < Etat.getColonne()) {
					ArrayList<Coup> coups = e.coupsPossibles(e);
					for(Coup c:coups){
						//System.out.println("ligne : "+c.getLigne()+" col : "+c.getColonne());
						if(c.getColonne() == j){
							
							choixJ = true;
							return c;
						}
					}
				} else {
					System.out.println("Vous devez saisir un entier entre 0 et " + Etat.getColonne());
				}
			} catch (InputMismatchException e2) {
				System.out.println("Vous devez saisir un entier");
			}
		}
		return null;
	}

}
