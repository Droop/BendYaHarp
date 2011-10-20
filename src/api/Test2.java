package src.api;

import static src.core.NoteChrom.DO;
import static src.core.NoteChrom.DOd;
import static src.core.NoteChrom.FA;
import static src.core.NoteChrom.FAd;
import static src.core.NoteChrom.LA;
import static src.core.NoteChrom.LAd;
import static src.core.NoteChrom.MI;
import static src.core.NoteChrom.RE;
import static src.core.NoteChrom.REd;
import static src.core.NoteChrom.SI;
import static src.core.NoteChrom.SOL;
import static src.core.NoteChrom.SOLd;

import java.util.Arrays;
import java.util.List;


import src.core.Harmonica;
import src.core.HarpBuilder;
import src.core.NoteChrom;
public class Test2 {


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		HarpBuilder configurator= new HarpBuilder();
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
