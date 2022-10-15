package principle.DependencyInversion;

public class DependencyInversion {
    public static void main(String[] args) {
        Changhong changhong = new Changhong();
        //OpenAndClose oac = new OpenAndClose();
        //oac.open(changhong);
        ////2
        //OpenAndClose openAndClose = new OpenAndClose(changhong);
        //openAndClose.open();
        OpenAndClose openAndClose = new OpenAndClose();
        openAndClose.setTv(changhong);
        openAndClose.open();
    }
}

//1、使用接口
//interface IOpenAndClose {
//    public void open(ITv tv);
//}
//
//interface ITv {
//    public void play();
//}
//
//class OpenAndClose implements IOpenAndClose{
//    @Override
//    public void open(ITv tv) {
//        tv.play();
//    }
//}
//
//class Changhong implements ITv {
//    @Override
//    public void play() {
//        System.out.println("长虹电视，打开了");
//    }
//}


//2使用构造方法
//interface ITv {
//    public void play();
//}
//
//interface IOpenAndClose {
//    public void open();
//}
//
//class OpenAndClose implements IOpenAndClose{
//    public ITv tv;
//    public OpenAndClose(ITv tv) {
//        this.tv = tv;
//    }
//
//    @Override
//    public void open() {
//        this.tv.play();
//    }
//}
//
//class Changhong implements ITv {
//    @Override
//    public void play() {
//        System.out.println("长虹电视打开了");
//    }
//}
//3使用setter方法传递
interface ITv {
    public void play();
}

interface IOpenAndClose {
    public void open();
}

class OpenAndClose implements IOpenAndClose{
    public ITv tv;

    public void setTv(ITv tv) {
        this.tv = tv;
    }

    @Override
    public void open() {
        this.tv.play();
    }
}

class Changhong implements ITv {
    @Override
    public void play() {
        System.out.println("长虹电视打开了");
    }
}