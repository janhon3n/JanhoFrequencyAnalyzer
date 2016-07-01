import javax.swing.*;
import java.awt.*;

/**
 * Created by Miquel on 22.6.2016.
 */
public class Grapher extends JFrame {

    Graph timeGraph, freqGraph;

    //Edit these values for your data line so that graphs are a good size. (smaller values makes graphs bigger)
    double timeMaxValue = 80;
    double freqMaxValue = 3;

    public Grapher(){
        //set default setting for the JFrame
        this.setSize(600,400);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.getContentPane().setBackground(Color.BLACK);
        this.setVisible(true);
        this.setTitle("JanhoSignalAnalyzer");


        timeGraph = new Graph(Graph.DRAW_ORGIN.MIDDLE, Graph.STYLE.LINE);
        freqGraph = new Graph(Graph.DRAW_ORGIN.BOTTOM, Graph.STYLE.BAR);
        timeGraph.setMaxValue(timeMaxValue);
        freqGraph.setMaxValue(freqMaxValue);
        freqGraph.setShiftY(-1);
        this.add(timeGraph);
        this.add(freqGraph);
    }

    public Graph getTimeGraph(){
        return timeGraph;
    }

    public Graph getFreqGraph(){
        return freqGraph;
    }
}
