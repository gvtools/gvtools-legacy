package org.gvsig.graph.solvers;

import java.util.Random;

import org.gvsig.graph.core.GvFlag;

import com.sun.org.apache.xpath.internal.operations.Mod;

public class TspSolverAnnealing {
	static int T_INIT = 100;
	static double FINAL_T = 0.1;
	static double COOLING = 0.9; /* to lower down T (< 1) */

	
	int n;
	int[] iorder;
	int[] jorder;
	float[] b = new float[4];
	double[][] odMatrix;
	boolean bReturnToOrigin;
	int origenTSP, destinoTSP;
	int[] vTSP;



	/*
	 * State vars
	 */
	int     verbose = 0;
	private GvFlag[] flags  = null;
	private boolean bVolverOrigen = false;
	private double distTotMin = Double.MAX_VALUE;

	/* float calcula_dist_ordenacion(int v[], int bVolverOrigen)
	{
		float dist, distTot;
		int i;

	        distTot = odMatrix[origenTSP][v[0]]; // Origen al primer punto
	        for (i = 0; i< numElemTSP-1;i++)
			{
				dist = odMatrix[v[i]][v[i+1]];
	            distTot = distTot + dist;
			}
	        
	        //  desde y hasta el almacen (distancia al primero y al último
	        if (bVolverOrigen)
			{            
				dist = odMatrix[v[numElemTSP-1]][origenTSP];
	            distTot = distTot + dist;
	        }
			else
			{
				dist = odMatrix[v[numElemTSP-1]][destinoTSP];
				distTot = distTot + dist;
			}
			return distTot;
	} */


	double pathLength()
	{
		int i; 
		double len = 0;

		len = D(origenTSP, iorder[0]);
		for (i = 0; i < n-1; i++)
		{
			len += D(iorder[i], iorder[i+1]);
		}

		if (bReturnToOrigin)
			len += D(iorder[n-1], origenTSP); /* close path */
		else
			len += D(iorder[n-1], destinoTSP); /* close path */
		return (len);
	}

	
	int mod(int a, int b) {
		int aux = (a % b);
		if (aux < 0)
			aux = aux + b;
		return aux;
	}
	
	double D(int f, int t) {
		return odMatrix[f][t];
	}


	/*
	 * Local Search Heuristics
	 *  b-------a        b       a
	 *  .       .   =>   .\     /.
	 *  . d...e .        . e...d .  
	 *  ./     \.        .       .
	 *  c       f        c-------f
	 */
	double getThreeWayCost (int[] p)
	{
		int a, b, c, d, e, f;
		a = iorder[mod(p[0]-1, n)]; b = iorder[p[0]];
		c = iorder[p[1]];   d = iorder[mod(p[1]+1,n)];
		e = iorder[p[2]];   f = iorder[mod(p[2]+1,n)];
		
		// b va a ser el nuevo origen => sumamos su distancia a nuestro origen fijo
		double Dant, Dnuevo, Ddiff = 0;
		Dant = D(origenTSP, iorder[0]);
		Dnuevo = D(origenTSP, b);
		Ddiff = Dnuevo - Dant;

		// también hay que mirar la distancia al destino desde el último punto
		int fin=n-1;
		if (bReturnToOrigin)
		{	
			// p[2] va a ser el próximo último punto 
			Dant = D(iorder[fin], origenTSP);
			Dnuevo = D(iorder[p[2]], origenTSP);
			Ddiff = Ddiff + Dnuevo - Dant;
		}
		else
		{
			Dant = D(iorder[fin], destinoTSP);
			Dnuevo = D(iorder[p[2]], destinoTSP);
			Ddiff = Ddiff + Dnuevo - Dant;
		}
		

		return (D(a,d) + D(e,b) + D(c,f) - D(a,b) - D(c,d) - D(e,f) + Ddiff); 
	        /* add cost between d and e if non symetric TSP */ 

		// AÑADIR DIFERENCIA DE COSTE A LOS NODOS INMOVILES. (ORIGEN Y ¿DESTINO?)
	}

	void doThreeWay (int[] p)
	{
		int i, count, m1, m2, m3, a, b, c, d, e, f;
		int index;
		a = mod(p[0]-1,n); b = p[0];
		c = p[1];   d = mod(p[1]+1,n);
		e = p[2];   f = mod(p[2]+1,n);	
		
		m1 = mod(n+c-b,n)+1;  /* num cities from b to c */
		m2 = mod(n+a-f,n)+1;  /* num cities from f to a */
		m3 = mod(n+e-d,n)+1;  /* num cities from d to e */

		count = 0;
		/* [b..c] */
		for (i = 0; i < m1; i++)
		{
			index = mod(i+b,n);
			jorder[count++] = iorder[index];
		}

		/* [f..a] */
		for (i = 0; i < m2; i++)
		{
			index = mod(i+f,n);
			jorder[count++] = iorder[index];
		}

		/* [d..e] */
		for (i = 0; i < m3; i++)
		{
			index = mod(i+d,n);
			jorder[count++] = iorder[index];
		}

		/* copy segment back into iorder */
		for (i = 0; i < n; i++) iorder[i] = jorder[i];
	}

	/*
	 *   c..b       c..b
	 *    \/    =>  |  |
	 *    /\        |  |
	 *   a  d       a  d
	 */
	double getReverseCost (int[] p)
	{
		int a, b, c, d;
		a = iorder[mod(p[0]-1,n)]; b = iorder[p[0]];
		c = iorder[p[1]];   d = iorder[mod(p[1]+1,n)];
		
		double Dant = 0, Dnuevo = 0, Ddiff = 0;

		if (p[0]==0 || p[1]==0)
		{
			Dant = D(origenTSP,iorder[0]);
			if (p[0]==0)
				Dnuevo = D(origenTSP, c);
			else
				Dnuevo = D(origenTSP, b);
			Ddiff = Dnuevo-Dant;
		}
		int fin = n-1;
		if (bReturnToOrigin) // Miramos la distancia al cero
		{		
			// iorder[p[1]] o iorder[p[0]] va a acabar en la última posición
			if (p[0]==fin || p[1] == fin) // también hay que mirar la distancia al destino desde el último punto
			{
				Dant = D(iorder[fin], origenTSP);
				if (p[0]==fin)
					Dnuevo = D(c,origenTSP);
				else
					Dnuevo = D(b, origenTSP);
				Ddiff = Ddiff + Dnuevo - Dant;

			} 
		}
		else
		{
			if (p[0]==fin || p[1] == fin) // también hay que mirar la distancia al destino desde el último punto
			{
				Dant = D(iorder[fin], destinoTSP);
				if (p[0]==fin)
					Dnuevo = D(c,destinoTSP);
				else
					Dnuevo = D(b, destinoTSP);
				Ddiff = Ddiff + Dnuevo - Dant;

			} 
		} 

		return (D(d,b) + D(c,a) - D(a,b) - D(c,d) + Ddiff);
	        /* add cost between c and b if non symetric TSP */ 
//	 AÑADIR DIFERENCIA DE COSTE A LOS NODOS INMOVILES. (ORIGEN Y ¿DESTINO?)
	}

	void doReverse(int[] p)
	{
		int i, nswaps, first, last, tmp;

	        /* reverse path b...c */
		nswaps = (mod(p[1]-p[0],n)+1)/2;
		for (i = 0; i < nswaps; i++)
	        {
			first = mod(p[0]+i, n);
			last  = mod(p[1]-i, n);
			tmp   = iorder[first];
			
			iorder[first] = iorder[last];
			iorder[last]  = tmp;
	        }
	}

	double annealing()
	{
		int[] p = new int[3];
		int    i=1, j, pathchg;
		int    numOnPath, numNotOnPath;
		double    pathlen, bestlen;
		double energyChange, T;
		
		/*
		 * Set up first eulerian path iorder to be improved by
		 * simulated annealing. 
		 */
		/* bool conEulerian = true;
		if (conEulerian)
		 	findEulerianPath();  */


		pathlen = pathLength(); // (iorder); 
		bestlen = pathlen;
		Random rnd = new Random();
		
		int TRIES_PER_T = 500*n;   
		int IMPROVED_PATH_PER_T = 60*n;    

		for (T = T_INIT; T > FINAL_T; T *= COOLING)  /* annealing schedule */
	        {
			pathchg = 0;
			for (j = 0; j < TRIES_PER_T; j++)
			{
				do {
					p[0] = rnd.nextInt(n);
					p[1] = rnd.nextInt(n);
					if (p[0] == p[1])
						p[1] = mod(p[0]+1,n); /* non-empty path */
					numOnPath = mod(p[1]-p[0],n) + 1;
					numNotOnPath = n - numOnPath;
				} while (numOnPath < 2 || numNotOnPath < 2) ; /* non-empty path */
				
				double rreal = rnd.nextDouble();
				if ((rnd.nextInt() % 2) == 0) /*  threeWay */
				{
					do {
						p[2] = mod(rnd.nextInt(numNotOnPath)+p[1]+1,n);
					} while (p[0] == mod(p[2]+1,n)); /* avoids a non-change */
					
					energyChange = getThreeWayCost (p);
					if ((energyChange < 0) || (rreal < Math.exp(-energyChange/T)))
					{
						pathchg++;
						pathlen += energyChange;
						doThreeWay (p); 
					}
				}
				else            /* path Reverse */
				{
					energyChange = getReverseCost (p);
					if ((energyChange < 0) || (rreal < Math.exp(-energyChange/T)))
					{
						pathchg++;
						pathlen += energyChange;
						doReverse(p); 
					}
				}
				if (pathlen < bestlen)
				{
					pathlen = pathLength(); // Calculamos la distancia de verdad, por si no interesa
											// hacer el cambio. pathlen es en realidad una estimación
					if (pathlen < bestlen)
					{
						bestlen = pathlen;
						for (i=0; i< n; i++)
							vTSP[i+1] = iorder[i];
					}
				}
				if (pathchg > IMPROVED_PATH_PER_T) break; /* finish early */
			}   
			if (pathchg == 0) break;   /* if no change then quit */
	        }
		return bestlen;
	}


	public void setStops(GvFlag[] flags) {
		this.flags = flags;
	}


	/**
	 * This function will use annealing only if numStops >= 6. If numStops < 6, it will
	 * use direct calculation, ensuring the optimum result.
	 * @return
	 */
	public GvFlag[] calculate() {
		GvFlag[] orderedStops = new GvFlag[flags.length];
		int numStops = flags.length;
		int numElemTSP;
		origenTSP = 0;
		destinoTSP = numStops-1;
		if (bVolverOrigen )
			numElemTSP = numStops-1;		
		else
			numElemTSP = numStops-2;

		n = numElemTSP;

		bReturnToOrigin = bVolverOrigen;
		
		iorder = new int[n];
		jorder = new int[n];



		vTSP = new int[numStops];
		vTSP[0] = origenTSP;
		vTSP[numStops-1] = destinoTSP;
		if (numElemTSP > 0)
		{		
			// v = new int[numElemTSP];
			for (int i=0; i< numElemTSP; i++)
			{
				iorder[i] = i + 1;
				vTSP[i+1] = i + 1; // v[i];
			}

		} 

		/* identity permutation */
		// for (i = 0; i < n; i++) iorder[i] = i+1; 

		double distTotal=0.0;
		
		if (numElemTSP < 7)
		{		

			distTotMin = calculate_distance(iorder,bVolverOrigen);

			permut(iorder, numElemTSP, bVolverOrigen);
			
			distTotal = distTotMin ;
		}
		else
		{
			distTotal = annealing();
		}
		
		System.out.println("DistTotal = " + distTotal);
		System.out.print("new order = [");
		for (int i=0; i < vTSP.length; i++) {
			orderedStops[i] = flags[vTSP[i]];
			System.out.print(vTSP[i] + " ");
		}
		System.out.println("]");
		return orderedStops;
	}

	
	double calculate_distance(int v[], boolean bVolverOrigen)
	{
		double dist, distTot;
		int i;

	        distTot = D(origenTSP, v[0]); // Origen al primer punto
	        for (i = 0; i< n-1;i++)
			{
	            //frmDocMap.Distancia v(i), v(i + 1), dist, tiempo
				dist = D(v[i], v[i+1]);
	            distTot = distTot + dist;
			}
	        
	        //  desde y hasta el almacen (distancia al primero y al último
	        if (bVolverOrigen)
			{            
				dist = D(v[n-1], origenTSP);
	            distTot = distTot + dist;
	        }
			else
			{
				dist = D(v[n-1], destinoTSP);
				distTot = distTot + dist;
			}
			return distTot;
	}

	void exchange(int v[], int p1, int p2)
	{
	  int aux;
	  aux = v[p1];
	  v[p1] = v[p2];
	  v[p2] = aux;
	}

	 void permut (int v[], int m, boolean bVolverOrigen)
	{
	  int i, j;
	  double distTot;

	  if (m > 0)
	    for (i = 0; i < m; i++)
	      {
	    	exchange(v, i, m-1);
	        permut (v, m-1, bVolverOrigen);
	        exchange(v, i, m-1);
	      }
	  else
	  {
	     // escribir_vector (v);
	     // Aquí tenemos el vector que buscamos
	     // Calculamos su distancia y si es menor que la que tenemos guardada, guardamos el nuevo vector.
		  distTot = calculate_distance(v, bVolverOrigen);
	                
	        if (distTot < distTotMin)
			{
	            distTotMin = distTot;
	            for (j = 0; j< n; j++)
	                vTSP[j+1] = v[j];
	        }
	        // 'MuestraVector v

	  }
	}


	public void setODMatrix(double[][] odMatrix) {
		this.odMatrix = odMatrix;
	}


	/**
	 * @return true if the path is closed => will return to origin.
	 * @see setReturnToOrigin
	 */
	public boolean isClosed() {
		return bReturnToOrigin;
	}


	public void setReturnToOrigin(boolean returnToOrigin) {
		bReturnToOrigin = returnToOrigin;
	}

}
