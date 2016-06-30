/**
 * Created by Miquel on 30.6.2016.
 */
public class Complex {

    private double real, img;

    public Complex(double real, double img){
        this.real = real;
        this.img = img;
    }

    public Complex plus(Complex c){
        return new Complex(this.getReal() + c.getReal(), this.getImg() + c.getImg());
    }
    public Complex minus(Complex c){
        return new Complex(this.getReal() - c.getReal(), this.getImg() - c.getImg());
    }
    public Complex times(Complex c){
        return new Complex((this.getReal() * c.getReal()) - (this.getImg() * c.getImg()), (this.getReal() * c.getImg()) + (c.getReal() * this.getImg()));
    }

    public double getAbs() {
        return Math.sqrt(getReal() * getReal() + getImg() * getImg());
    }
    public double getReal(){
        return real;
    }
    public double getImg(){
        return img;
    }
}
