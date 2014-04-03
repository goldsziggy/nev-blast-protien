/*
 * 
    This file is part of NEVBLAST.

    NEVBLAST is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    NEVBLAST is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NEVBLAST.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

/**
@BlastQuery.java
    This class takes the userinput and performs a BLAST query.  Once the Blast Object 
    is returned from BLAST it is then parsed and an ArrayList of SequenceHit objects are created.
    These objects hold all the information that blast has passed it.  It is here that
    the BLAST perform the signature score calculation.

*/
package group4.nevblast;

import BLAST.BlastOutput;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.biojava3.core.sequence.io.util.IOUtils;
import org.biojava3.ws.alignment.qblast.*;
import static org.biojava3.ws.alignment.qblast.BlastAlignmentParameterEnum.ALIGNMENTS;
import static org.biojava3.ws.alignment.qblast.BlastAlignmentParameterEnum.ENTREZ_QUERY;
import static org.biojava3.ws.alignment.qblast.BlastAlignmentParameterEnum.MAX_NUM_SEQ;
/**
 * @author Matthew Zygowicz - Ziggy
 * @adapted from: www.alextblog.blogspot.com/2013/05/ncbi-blast-jaxb-biojava-blasting-like.html
 */
public class BlastQuery {
    public String status="";
    private final String queryFld_;
    private final Signature sigA_;
    private final Signature sigB_;
    private double sigScoreA_;
    private double sigScoreB_;
    private final String fastaSequence_;
    private final BigDecimal EValue_;
    private final String numberOfResults_;
    private final String sugSigs_;
    private final String fastaHeader_;
    private final MySubstitutionMatrixHelper blosum;
    private int normalizedBlosumScoreA;
    private int normalizedBlosumScoreB;
    private String blastProgram;
    private String blastDatabase;
    private String entrezQuery;
   
    // Create a stream to hold the output

    //This is the constructor to create a BLAST Query     
    //each signature is an arraylist of AminoAcid/LineNumber combinations.  The complete arraylist makes up the signature
    public BlastQuery(String queryFld,  Signature sigA,  Signature sigB, String fSequence, String fHeader, BigDecimal EVal, String nResults, String sugSigs,String matrixType,int[][] userMatrix, String program, String database, String entrezQueryParam) {
        queryFld_        = queryFld;
        sigA_            = sigA;
        sigB_            = sigB;
        fastaSequence_   = fSequence;
        fastaHeader_     = fHeader;
        EValue_            = EVal;
        numberOfResults_ = nResults;                                             //nResults = Number of Results
        sugSigs_         = sugSigs;
        blosum = new MySubstitutionMatrixHelper(matrixType,userMatrix);
        blastProgram = program;
        blastDatabase = database;
        this.entrezQuery = entrezQueryParam;
        //get the best possible score
        for(int i = 0; i < sigA_.size();i++){
            normalizedBlosumScoreA +=  blosum.getCharScore(sigA_.get(i).getAminoAcid(),sigA_.get(i).getAminoAcid());
        }
        for(int i = 0; i < sigB_.size();i++){
            normalizedBlosumScoreB += blosum.getCharScore(sigB_.get(i).getAminoAcid(),sigB_.get(i).getAminoAcid());
        }
        /*
         * TEST Normalization
         */
        //System.out.println("Normalized A: " + normalizedBlosumScoreA);
        //System.out.println("Normalized B: " + normalizedBlosumScoreB);
        
        
    }//end custom constructor 

    /* -- Getters -- */
    public String getQueryFld() {
        return queryFld_;
    }

    public Signature getSigA() {
        return sigA_;
    }

    public Signature getSigB() {
        return sigB_;
    }

    public String getFasta() {
        return fastaSequence_;
    }

    public BigDecimal getEVal() {
        return EValue_;
    }

    public int getNumberOfResults() {
        return Integer.valueOf(numberOfResults_);
    }

    public String getSugSigs() {
        return sugSigs_;
    }

    public ArrayList<SequenceHit> toBlast() throws Exception {
        ArrayList<SequenceHit> toGraph = new ArrayList<SequenceHit>();
        BlastOutput BO;
        int numberOfResults = getNumberOfResults();


        NCBIQBlastService service = new NCBIQBlastService();

        // set alignment options
        NCBIQBlastAlignmentProperties props = new NCBIQBlastAlignmentProperties();
        
        
        
        if(blastProgram == "blastp")
            props.setBlastProgram(BlastProgramEnum.blastp);
        else if(blastProgram == "blastn")
            props.setBlastProgram(BlastProgramEnum.blastn);
        else if(blastProgram == "blastx")
            props.setBlastProgram(BlastProgramEnum.blastx);
        else if(blastProgram == "megablast")
            props.setBlastProgram(BlastProgramEnum.megablast);
        else if(blastProgram == "tblastn")
            props.setBlastProgram(BlastProgramEnum.tblastn);
        else if(blastProgram == "tblastx")
            props.setBlastProgram(BlastProgramEnum.tblastx);


        props.setBlastDatabase(blastDatabase);
        
        //get blast DB from http://blast.ncbi.nlm.nih.gov/Blast.cgi?CMD=Web&PAGE_TYPE=BlastDocs&DOC_TYPE=ProgSelectionGuide


     //   props.setAlignmentOption(ENTREZ_QUERY, fastaHeader_);
        if(this.entrezQuery.trim().length() > 0){
            props.setAlignmentOption(ENTREZ_QUERY,this.entrezQuery.trim());
        }
        props.setAlignmentOption(ALIGNMENTS, String.valueOf(numberOfResults));
        props.setAlignmentOption(MAX_NUM_SEQ, String.valueOf(numberOfResults) );
    //    props.setBlastWordSize(3);

        // set output options
        NCBIQBlastOutputProperties outputProps = new NCBIQBlastOutputProperties();

        outputProps.setOutputOption(BlastOutputParameterEnum.ALIGNMENTS, String.valueOf(numberOfResults));
        outputProps.setOutputOption(BlastOutputParameterEnum.DESCRIPTIONS,String.valueOf(numberOfResults));
        
        String rid = null;          // blast request ID
        FileWriter writer = null;
        BufferedReader reader = null;
        try {
            // send blast request and save request id
            rid = service.sendAlignmentRequest(fastaSequence_, props);

            System.out.println("The newly submitted BLAST had a RID: " + rid);//This is just to display where goes the RID after you have submitted a new BLAST
            System.out.println(service.getRemoteBlastInfo());

            // wait until results become available. Alternatively, one can do other computations/send other alignment requests
            while (!service.isReady(rid)) {
                status = "Waiting for results. Sleeping for 5 seconds";
                System.out.println(status);
                Thread.sleep(5000);
                status = ""; //reset;
                
            }

            // read results when they are ready
            InputStream in = service.getAlignmentResults(rid, outputProps);
         
            //---------------MyCode brakes the BioJava example here -----------------------
            BO = BlastQuery.catchBLASTOutput(in);
            System.out.println("OUTPUT DATABASE");
            System.out.println(BO.getBlastOutputDb());
  
            System.out.println("OUTPUT PROGRAM");
            System.out.println(BO.getBlastOutputProgram());
            System.out.println("OUTPUT QUERY DEFINITION");
            System.out.println(BO.getBlastOutputQueryDef());
            System.out.println("OUTPUT QUERY ID");
            System.out.println(BO.getBlastOutputQueryID());
            System.out.println("OUTPUT QUERY LENGTH");
            System.out.println(BO.getBlastOutputQueryLen());
            System.out.println("OUTPUT QUERY SEQUENCE");
            System.out.println(BO.getBlastOutputQuerySeq());
            System.out.println("OUTPUT QUERY REFERENCE");
            System.out.println(BO.getBlastOutputReference());
            for(int i = 0; i < BO.getBlastOutputIterations().getIteration().size(); i++){

                for(int k = 0; k < BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().size(); k++){
                    
                    System.out.println("\n\n\n");
                    System.out.println("GET HIT NUM");
                    System.out.println(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitNum());
                    System.out.println("GET HIT LENGTH");
                    System.out.println(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitLen());
                    System.out.println("GET HIT ID");
                    System.out.println(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitId());
                    String accession = BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitAccession();
                    ///need to iterate HSPS
                    System.out.println("--------------GET HIT HSPS-------------------");
                    for(int j = 0; j < BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().size(); j++){
                        System.out.println("EVALUE");
                        System.out.println(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspEvalue());
                        BigDecimal tempEvalue = new BigDecimal(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspEvalue());                  
                        if (tempEvalue.compareTo(EValue_) <= 0){
                            System.out.println("H Sequence");
                            System.out.println(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspHseq());
                            String hitSeq = BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspHseq();
                            System.out.println("HIT FROM");
                            System.out.println(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspHitFrom());
                            String hitFrom = BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspHitFrom();
                            System.out.println("HIT TO");
                            String hitTo = BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspHitTo();
                            System.out.println(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspHitTo());

                            System.out.println("Q Sequence");
                            System.out.println(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQseq()); 
                            System.out.println("QUERY FROM");
                            System.out.println(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQueryFrom());
                            System.out.println("QUERY TO");
                            System.out.println(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQueryTo());


                            System.out.println("Alignment Length");
                            System.out.println(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspAlignLen());

                            /*
                             * BEGIN GETTINGS SIGA SCORE
                             */
                            int scorea = 0;
                            for(int p = 0; p < sigA_.size(); p++){
                                if(sigA_.get(p).getLineNumber() <= Integer.valueOf(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQueryTo())){
                                   if(sigA_.get(p).getLineNumber() >= Integer.valueOf(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQueryFrom())) {
                                       //Number is valid!
                                       //position = lineNumber of search - line number query begins at

                                       int position = sigA_.get(p).getLineNumber() - Integer.valueOf(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQueryFrom());
                                       System.out.println("sigA LineNumber = " + sigA_.get(p).getLineNumber());
                                       System.out.println("sigA BO QueryHitFrom = " + Integer.valueOf(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQueryFrom()));
                                       System.out.println("position = " + position);
                                       
                                       String qHit = BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQseq();
                                       int originalPosition = position;
                                       for(int y = 0; y < originalPosition; y++){
                                           if(qHit.charAt(y) == '-'){
                                               position++;
                                           }
                                       }
         
               
                                   
                                       scorea  += blosum.getCharScore(sigA_.get(p).getAminoAcid(), BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspHseq().charAt(position));
                                   }//end if greater then or equal to queryFrom
                                   else{
                                       //number is to  small
                                   }
                                }//end if less then the queryTo
                                else{
                                    //number is to larger
                                }           
                            }//end for p - sigA

                            System.out.println("Signature A score: " + scorea );
                            
                            System.out.println("Signature A score normalized (" + scorea + " / " + normalizedBlosumScoreA + ") : " + (double)scorea/(double)normalizedBlosumScoreA);
                            /*
                             * BEGIN GETTINGS SIGB SCORE
                             */
                            int scoreb = 0;
                            for(int q = 0; q < sigB_.size(); q++){
                                if(sigB_.get(q).getLineNumber() <= Integer.valueOf(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQueryTo())){
                                   if(sigB_.get(q).getLineNumber() >= Integer.valueOf(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQueryFrom())) {
                                       //Number is valid!
                                       //position = lineNumber of search - line number query begins at
                                       int position = sigB_.get(q).getLineNumber() - Integer.valueOf(BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQueryFrom());
                                       String qHit = BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspQseq();
                                       int originalPosition = position;
                                       for(int y = 0; y < originalPosition; y++){
                                           if(qHit.charAt(y) == '-'){
                                               position++;
                                           }
                                       }
                                       scoreb += blosum.getCharScore(sigB_.get(q).getAminoAcid(), BO.getBlastOutputIterations().getIteration().get(i).getIterationHits().getHit().get(k).getHitHsps().getHsp().get(j).getHspHseq().charAt(position));

                                   }//end if greater then or equal to queryFrom
                                   else{
                                       //number is to  small
                                   }
                                }//end if less then the queryTo
                                else{
                                    //number is to larger
                                }           
                            }//end for q - sigB
                           System.out.println("Signature B score: " + scoreb );
                           System.out.println("Signature B score normalized (" + scoreb + " / " + normalizedBlosumScoreB + ") : " + (double)scoreb/(double)normalizedBlosumScoreB);

                           SequenceHit temp = new SequenceHit(accession,hitSeq,hitFrom,hitTo,tempEvalue.toString(),Double.toString(scorea),Double.toString(scoreb));
                           temp.setNormalizedScoreA((double)scorea/(double)normalizedBlosumScoreA);
                           temp.setNormalizedScoreB((double)scoreb/(double)normalizedBlosumScoreB);
                           toGraph.add(temp);
                        }

                        System.out.println("\n\n");
                        System.out.println("j = " + j);
                    }//end j - iterate HSPS
                    System.out.println("k = " + k);
                }//end for k
                System.out.println("i = " + i);
            }  //end for i        
                    
            System.out.println("");
            //we only had one BLAST query, so we are expecting only one iteration, that's why .get(o).
            //same thing about the .getHit().get(0) - we are asking for the best Hit, which is first on the list
            //etc, just use the BO getters
            //---------------MyCode brakes the BioJava example here -----------------------\\

        } catch (Exception e) {
            
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("blastquery exception");
            SequenceHit errorSequence = new SequenceHit();
            errorSequence.setError(e.getMessage());
            toGraph.clear();
            toGraph.add(errorSequence);
            return toGraph;
            
        } finally {
            // clean up
            IOUtils.close(writer);
            IOUtils.close(reader);
            
            service.sendDeleteRequest(rid);
            return toGraph;
        }

    }//end toBlast

    private static BlastOutput catchBLASTOutput(InputStream in) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(BlastOutput.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (BlastOutput) u.unmarshal(in);
    }
    
}//end BlastQuery Class

