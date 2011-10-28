package src.api;

import static src.harmonica.NoteChrom.DO;
import static src.harmonica.NoteChrom.DOd;
import static src.harmonica.NoteChrom.FA;
import static src.harmonica.NoteChrom.FAd;
import static src.harmonica.NoteChrom.LA;
import static src.harmonica.NoteChrom.LAd;
import static src.harmonica.NoteChrom.MI;
import static src.harmonica.NoteChrom.RE;
import static src.harmonica.NoteChrom.REd;
import static src.harmonica.NoteChrom.SI;
import static src.harmonica.NoteChrom.SOL;
import static src.harmonica.NoteChrom.SOLd;

import java.util.Arrays;
import java.util.List;


import src.builder.HarmonicaBuilder;
import src.harmonica.Harmonica;
import src.harmonica.NoteChrom;
public class Test2 {


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		HarmonicaBuilder configurator= new HarmonicaBuilder();
		configurator.addHole(RE, 3, MI, 4);
		configurator.addHole(FAd, 3, SOL, 4);
		configurator.addHole(LA, 3, SI, 4);
		configurator.addHole(RE, 4, RE, 5);
		configurator.addHole(FAd, 4, FAd, 5);
		configurator.addHole(LA, 4, LA, 5);
		configurator.addHole(SI, 4, DO, 6);
		configurator.addHole(RE, 5, MI, 6);
		configurator.addHole(FA, 5, SOL, 6);
		configurator.addHole(LA, 5, DO, 7);

		List<NoteChrom> drawPlate = Arrays.asList(
				new NoteChrom[]{RE,  SOL,  DO, FA,  LAd});

		List<NoteChrom> blowPlate = Arrays.asList(
				new NoteChrom[]{SI,  FA,   LA, RE,  SOL });

			Harmonica harp = 
				new Harmonica(blowPlate, drawPlate, 
						10,	0, configurator.getFirstNote()).transpose(19).adjust(configurator.get(0));

//			System.out.println("testing : start point "+startingpoint+" transp "+demitons);
			System.out.println(harp);
			//				if (configurator.isPossible(harp)){
			//					System.err.println("HarpFound! : start point "+startingpoint+" transp "+demitons);
			//					System.out.println(harp);
			//					System.out.flush();
			//				}
			//					System.out.println();
		
		System.out.println("fin");
	}
}
