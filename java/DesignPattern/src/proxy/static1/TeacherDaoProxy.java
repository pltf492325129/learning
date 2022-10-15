package proxy.static1;

public class TeacherDaoProxy implements ITeacherDao{
    ITeacherDao teacherDao;
    public TeacherDaoProxy(ITeacherDao teacherDao) {
        this.teacherDao = teacherDao;
    }

    @Override
    public void teach() {
        System.out.println("使用代理类，动态的增加功能");
        teacherDao.teach();
    }
}
