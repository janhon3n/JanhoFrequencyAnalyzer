import javax.swing.*;
import java.awt.*;

/**
 * Created by Miquel on 22.6.2016.
 */
public class Graph extends JComponent {

    Color color = Color.WHITE;
    double[] data = new double[]{2,6,1,70,2,25,17,25};
    private DRAW_ORGIN draw_orgin = DRAW_ORGIN.BOTTOM;
    private STYLE style = STYLE.LINE;
    private double shiftY = 0;

    private double maxValue = 256;

    public enum DRAW_ORGIN {
        BOTTOM, MIDDLE, TOP
    }
    public enum STYLE {
        LINE, BAR
    }

    public Graph(){
    }
    public Graph(DRAW_ORGIN draw_orgin, STYLE style){
        this.draw_orgin = draw_orgin;
        this.style = style;
    }
    public Graph(DRAW_ORGIN draw_orgin, STYLE style, double maxValue){
        this.draw_orgin = draw_orgin;
        this.style = style;
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
        double deltaX;
        int lineCount;
        switch(style){
            case LINE:
                deltaX = width / (data.length - 1);
                lineCount = data.length - 1;
                break;
            case BAR:
            default:
                deltaX = width / (data.length);
                lineCount = data.length - 1;
                break;
        }


        for(int i = 0; i < lineCount; i++){
            int x1 = (int) (deltaX * i);
            int x2 = (int) (deltaX * (i+1));
            int y1, y2;

            switch(style) {
                case LINE:
                    y1 = (int) (data[i] / maxValue * height);
                    y2 = (int) (data[i + 1] / maxValue * height);
                    break;
                case BAR:
                default:
                    y1 = (int) (data[i] / maxValue * height);
                    y2 = (int) (data[i] / maxValue * height);
                    break;
            }

            switch(draw_orgin){
                case TOP:
                    break;
                case BOTTOM:
                    y1 = (int) (height - y1);
                    y2 = (int) (height - y2);

                    break;
                case MIDDLE:
                    y1 = (int) ((height / 2) - y1);
                    y2 = (int) ((height / 2) - y2);
            }

            y1 += shiftY;
            y2 += shiftY;
            g.drawLine(x1, y1, x2, y2);
        }
    }


    public DRAW_ORGIN getDrawOrgin() {
        return draw_orgin;
    }
    public void setDrawOrgin(DRAW_ORGIN style) {
        this.draw_orgin = style;
    }

    public void setStyle(STYLE style) {
        this.style = style;
    }
    public STYLE getStyle(){
        return this.style;
    }

    public double getShiftY() {
        return shiftY;
    }

    public void setShiftY(double shiftY) {
        this.shiftY = shiftY;
    }

    public double getMaxValue() {
        return maxValue;
    }
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
}
