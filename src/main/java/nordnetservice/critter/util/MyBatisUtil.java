package nordnetservice.critter.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyBatisUtil {

    private static String conf = "mybatis.conf.xml";

    private static SqlSessionFactory _factory = null;

    public static void setConfigFile(String configFile) {
        conf = configFile;
    }
    public static SqlSessionFactory getFactory() {
        if (_factory == null) {
            Reader reader = null;
            try {
                reader = Resources.getResourceAsReader(conf);
                SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
                _factory = builder.build(reader);
            } catch (IOException ex) {
                Logger.getLogger(MyBatisUtil.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(MyBatisUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return _factory;
    }
    public static SqlSession getSession() {
        return getFactory().openSession();
    }
    public static <T> T withSession(Function<SqlSession,T> fn) {
        SqlSession session = null;
        try {
            session = getSession();
            return fn.apply(session);
        }
        /*
        catch (Exception ex) {
            if (errorHandler == null){
                Logger.getLogger(MyBatisUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
            else {
                errorHandler.accept(ex);
            }
            return null;
        }

         */
        finally {
            if (session != null) {
                session.commit();
                session.close();
            }
        }
    }

    public static void withSessionConsumer(Consumer<SqlSession> consumer) {
        SqlSession session = null;
        try {
            session = getSession();
            consumer.accept(session);
        }
        /*
        catch (Exception ex) {
            if (errorHandler == null){
                Logger.getLogger(MyBatisUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
            else {
                errorHandler.accept(ex);
            }
        }
         */
        finally {
            if (session != null) {
                session.commit();
                session.close();
            }
        }
    }

}
