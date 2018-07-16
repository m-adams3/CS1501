/*************************************************************************
 *  Author: Michael Adams
 *
 *  Compilation:  javac MyLZW.java
 *  Execution:    java MyLZW - <mode> < input.txt > output.lzw   (compress)
 *  Execution:    java MyLZW + <mode> < input.lze > output.txt   (expand)
 *  Dependencies: BinaryStdIn.java BinaryStdOut.java TST.java Queue.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *************************************************************************/
 
public class MyLZW {
    private static final int R = 256;		// number of input chars
    private static int L;					// number of codewords = 2^W (these now vary)
    private static int W;					// codeword width (these now vary)

    public static void compress() { // DO NOTHING MODE COMPRESS
        String input = BinaryStdIn.readString();	// reads input file to a string
		BinaryStdOut.write('n'); // write compression type to output file
        TST<Integer> st = new TST<Integer>();	 
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
			//conditonally expand codebook (max number of codewords but not max codeword length)
			if(code == L && W < 16) {
				W++;
				L *= 2;
			}
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }
	
	public static void resetCompress() { // RESET MODE COMPRESS
        String input = BinaryStdIn.readString();	// reads input file to a string
		BinaryStdOut.write('r'); // write compression type to output file
		TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
			//conditonally expand codebook (max number of codewords but not max codeword length)
			if (code == L && W < 16) {
				W++;
				L *= 2;
			}
			//reset codebook (max number of codewords and max codeword length)
			else if (code == L && W == 16){
				L = 512;	// reset number of codewords
				W = 9;		// reset length of codewords
				st = new TST<Integer>(); // create new codebook
				for (int i = 0; i < R; i++)
					st.put("" + (char) i, i);
				code = R+1;  // R is codeword for EOF
			}
			//do normal stuff
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }
	
	public static void monitorCompress() { // MONITOR MODE COMPRESS
        String input = BinaryStdIn.readString();	// reads input file to a string
		BinaryStdOut.write('m'); // write compression type to output file
		//initialize variable to keep track of stuff
		boolean monitoring = false;	//flag for when to start monitoring
		double currRatio = 1.0;		//measures current compression performance
		double checkRatio = 1.0;	//measures ratio when codebook first filled
		int uSize = 0;				//size of uncompressed data handled so far
		int cSize = 0;				//size of handled data after compression
		
        TST<Integer> st = new TST<Integer>();	 
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
			//conditonally expand codebook (max number of codewords but not max codeword length)
			if(code == L && W < 16) {
				W++;
				L *= 2;
			}
			//begin monitoring ratio when codebook is full
			else if (code == L && W == 16 && !monitoring) {
				monitoring = true;
				checkRatio = currRatio; //checkRatio is now == to ratio when codebook filled
			}
			//conditonally reset codebook (old ratio/new ratio > 1.1)
			else if (code == L && W == 16 && monitoring && (checkRatio/currRatio > 1.1)) {
				L = 512;	// reset number of codewords
				W = 9;		// reset length of codewords
				st = new TST<Integer>(); // create new codebook
				for (int i = 0; i < R; i++)
					st.put("" + (char) i, i);
				code = R+1;  // R is codeword for EOF
				monitoring = false;	// reset monitoring flag
				checkRatio = 1.0; // reset this to arbitrary
			}
			//do normal stuff			
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
			uSize += s.length() * 8;			   // prefix length * 8 bits = size of uncompressed data handled so far
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
			cSize += W;							   // add length of codeword to size of compressed data handled so far
            int t = s.length();
            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
            input = input.substring(t);            // Scan past s in input.
			
			currRatio = (double)uSize/cSize;	   // currRatio = length of data read in * 8 bits / size of codewords written
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() { // DO NOTHING MODE EXPAND
        String[] st = new String[65536]; //256^2
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
			
			//conditonally expand codebook
			if (W < 16 && i == ((Math.pow(2,W)) - 1)) {
				L *= 2;
				W++;
			}
        }
        BinaryStdOut.close();
    }
	
	public static void resetExpand() { // RESET MODE EXPAND
        String[] st = new String[65536]; //256^2
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
			
			//conditonally expand codebook
			if (W < 16 && i == ((Math.pow(2,W)) - 1)) {
				L *= 2;
				W++;
			}
			//conditonally reset codebook
			if (W == 16 && i == ((Math.pow(2,W)) - 1)) {
				L = 512;	// reset number of codewords
				W = 9;		// reset length of codewords
				//do normal stuff
				st = new String[65536];
				for (i = 0; i < R; i++)
					st[i] = "" + (char) i;
				st[i++] = "";  
				BinaryStdOut.write(val); 
				codeword = BinaryStdIn.readInt(W);
				if (codeword == R) return;           // expanded message is empty string
				val = st[codeword];
			}
        }
        BinaryStdOut.close();
    }

	public static void monitorExpand() { // MONITOR MODE EXPAND
        String[] st = new String[65536]; //256^2
        int i; // next available codeword value
		
		//initialize variable to keep track of stuff
		boolean monitoring = false;	//flag for when to start monitoring
		double currRatio = 1.0;		//measures current compression performance
		double checkRatio = 1.0;	//measures ratio when codebook first filled
		int uSize = 0;				//size of uncompressed data handled so far
		int cSize = 0;				//size of handled data after compression

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
		uSize += W;
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
			//do I/O stuff
			try {
				BinaryStdOut.write(val);
			} catch (NullPointerException e) {
				System.err.println("Problem " + e);
			}
			cSize += val.length() * 8;
            codeword = BinaryStdIn.readInt(W);
            uSize += W;
			//do normal stuff
			if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
			//conditonally expand codebook
			if (W < 16 && i == ((Math.pow(2,W)) - 1)) {
				L *= 2;
				W++;
			}
			//begin monitoring ratio when codebook is full
			else if (i == (Math.pow(2,W)) && W == 16 && !monitoring) {
				monitoring = true;
				checkRatio = currRatio; //checkRatio is now == to ratio when codebook filled
			}
			//conditonally reset codebook (old ratio/new ratio > 1.1)
			if (monitoring && (checkRatio/currRatio > 1.1)) {
				L = 512;	// reset number of codewords
				W = 9;		// reset length of codewords
				i = 0;		// reset index
				st = new String[65536]; // create new string
				for (i = 0; i < R; i++)
					st[i] = "" + (char) i;
				st[i++] = "";
				//write data and keep track of size
				BinaryStdOut.write(val);
				cSize += val.length() * 8;
				//read data and keep track of size
				codeword = BinaryStdIn.readInt(W);
				uSize += W;
				if (codeword == R) return;           // expanded message is empty string
				val = st[codeword];
				monitoring = false;
			}
			currRatio = (double)uSize/cSize;	   // currRatio = length of data read in * 8 bits / size of codewords written
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
		L = 512;	// set number of codewords
		W = 9;		// set length of codewords
        if      (args[0].equals("-")) chooseCompressMode(args);
        else if (args[0].equals("+")) chooseExpandMode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
	
	//checks command line argument to detemine compress mode
	public static void chooseCompressMode(String[] args){
		char compressMode = args[1].charAt(0);
		//System.out.println("compressMode: " + compressMode);
		if (compressMode == 'n')
			compress();
		else if (compressMode == 'r')
			resetCompress();
		else if (compressMode == 'm')
			monitorCompress();
		else throw new IllegalArgumentException("you chose an invalid compress mode");
	}
	
	//checks first char of input file to determine expand mode
	public static void chooseExpandMode(){
		char expandMode = BinaryStdIn.readChar();
		//System.out.println("expandMode: " + expandMode);
		if (expandMode == 'n')
			expand();
		else if (expandMode == 'r')
			resetExpand();
		else if (expandMode == 'm')
			monitorExpand();
		else throw new IllegalArgumentException("you chose an invalid expansion mode");
	}
}
