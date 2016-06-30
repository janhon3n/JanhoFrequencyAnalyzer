import javax.swing.*;
import java.awt.*;

/**
 * Created by Miquel on 22.6.2016.
 */
public class Graph extends JComponent {

    Color color = Color.WHITE;
    double[] data = new double[]{2,6,1,70,2,25,17,25};
    private DRAW_ORGIN draw_orgin = DRAW_ORGIN.BOTTOM;

    private double maxValue = 256;

    public enum DRAW_ORGIN {
        BOTTOM, MIDDLE, TOP
    }

    public Graph(){
    }
    public Graph(DRAW_ORGIN draw_orgin){
        this.draw_orgin = draw_orgin;
    }
    public Graph(DRAW_ORGIN draw_orgin, double maxValue){
        this.draw_orgin = draw_orgin;
        this.maxValue = maxValue;
    }

    public void setData(double[] data){
        this.data = data;
        this.revalidate();
        this.repaint();
    }

    @Override
    public void paint(Graphics g){
        g.setColor(color);

        double height = this.getHeight();
        double width = this.getWidth();
        double deltaX = width / (data.length - 1);

        switch(draw_orgin){
            case TOP:
                for(int i = 0; i < data.length - 1; i++){
                    int x1 = (int) (deltaX * i);
                    int x2 = (int) (deltaX * (i+1));
                    int y1 = (int) (data[i] / maxValue * height);
                    int y2 = (int) (data[i+1] / maxValue * height);
                    g.drawLine(x1, y1, x2, y2);
                }
                break;
            case BOTTOM:
                for(int i = 0; i < data.length - 1; i++){
                    int x1 = (int) (deltaX * i);
                    int x2 = (int) (deltaX * (i+1));
                    int y1 = (int) (height - (data[i] / maxValue * height));
                    int y2 = (int) (height - (data[i+1] / maxValue * height));
                    g.drawLine(x1, y1, x2, y2);
                }
                break;
            case MIDDLE:
                for(int i = 0; i < data.length - 1; i++){
                    int x1 = (int) (deltaX * i);
                    int x2 = (int) (deltaX * (i+1));
                    int y1 = (int) ((height / 2) - (data[i] / maxValue * height));
                    int y2 = (int) ((height / 2) - (data[i+1] / maxValue * height));
                    g.drawLine(x1, y1, x2, y2);
                }

        }
    }


    public DRAW_ORGIN getStyle() {
        return draw_orgin;
    }
    public void setStyle(DRAW_ORGIN style) {
        this.draw_orgin = style;
    }

    public double getMaxValue() {
        return maxValue;
    }
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
}
