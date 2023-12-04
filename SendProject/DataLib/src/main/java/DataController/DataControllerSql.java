package DataController;

import Subs.PersonalInfoClass;
import com.google.protobuf.Message;
import entity.*;
import jakarta.persistence.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class DataControllerSql implements IDataController {
    public static Statement sq;
    String s_res;
    Connection db;
    StringBuffer x;
    String causeAllocation = "0";
    EntityManagerFactory managerFactory;
    EntityManager entityManager;
    EntityTransaction transaction;
    BufferedImage globalImage;
    List<BufferedImage> globalImageList = new ArrayList<>();
    List<String> globalNameSernameList = new ArrayList<>();
    public DataControllerSql() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        db = DriverManager.getConnection("jdbc:mysql://localhost:3306/practice", "root", "Gfhjkm159357");
        sq = db.createStatement();
        managerFactory = Persistence.createEntityManagerFactory("default");
        entityManager = managerFactory.createEntityManager();
        transaction = entityManager.getTransaction();
        x = new StringBuffer();
    }

    public Object GetSomething() {
        return "SELECT ...";
    }

    public Object Registration(String[] arrStr) {
        transaction.begin();
        TypedQuery<PersonalEntity> query = entityManager.createQuery("SELECT e FROM PersonalEntity e where e.login =:login AND e.nameSername =:name and e.contacts =:contacts and e.email =:email", PersonalEntity.class);
        query.setParameter("login", arrStr[1]);
        query.setParameter("name", arrStr[3]);
        query.setParameter("contacts", arrStr[4]);
        query.setParameter("email", arrStr[5]);
        List<PersonalEntity> resultPersonal = null;
        resultPersonal = query.getResultList();

        if (resultPersonal.isEmpty()) {
            PersonalEntity personal = new PersonalEntity();
            personal.setLogin(arrStr[1]);
            personal.setPassword(arrStr[2]);
            personal.setNameSername(arrStr[3]);
            personal.setContacts(arrStr[4]);
            personal.setEmail(arrStr[5]);
            personal.setSubrole("Заявка");
            personal.setRole("obey");
            personal.setStatus("Ожидает подтверждения");
            entityManager.persist(personal);
            s_res = "1/" + "\r";
            //==================================Notification==========================
        } else {
            s_res = "0/" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object Login(String[] arrStr) {
        transaction.begin();
        TypedQuery<PersonalEntity> q = entityManager.createQuery("SELECT e from PersonalEntity e WHERE e.login =:login AND e.password =:password", PersonalEntity.class);
        q.setParameter("login", arrStr[1]);
        q.setParameter("password", arrStr[2]);
        try {
            PersonalEntity entity = q.getSingleResult();
            if (entity.getStatus().equals("Активен")) {
                s_res = "success" + "<<" + entity.getRole() + "<<" + entity.getNameSername() + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (Exception e) {
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetPersonalList(String[] arrStr) {
        s_res = "";
        transaction.begin();
        TypedQuery<PersonalEntity> q = entityManager.createQuery("SELECT e from PersonalEntity e", PersonalEntity.class);
        try {
            List<PersonalEntity> list = q.getResultList();
            for (PersonalEntity personal : list) {
                s_res += personal.getNameSername() + ">>" + personal.getContacts() + ">>" + personal.getEmail() + ">>" + personal.getSubrole() + ">>" + personal.getStatus() + ">>" + personal.getLogin() + ">>" + personal.getPassword();
                s_res += "<<";
            }
            s_res += "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object AddPersonal(String[] arrStr) {
        transaction.begin();
        TypedQuery<PersonalEntity> query = entityManager.createQuery("SELECT e FROM PersonalEntity e where e.login =:login or e.nameSername =:name", PersonalEntity.class);
        query.setParameter("login", arrStr[1]);
        query.setParameter("name", arrStr[3]);
        List<PersonalEntity> resultPersonal = null;
        resultPersonal = query.getResultList();

        if (resultPersonal.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            PersonalEntity personal = new PersonalEntity();
            personal.setLogin(arrStr[1]);
            personal.setPassword(arrStr[2]);
            personal.setNameSername(arrStr[3]);
            personal.setContacts(arrStr[4]);
            personal.setEmail(arrStr[5]);
            personal.setSubrole(arrStr[7]);
            personal.setRole(arrStr[6]);
            personal.setStatus(arrStr[8]);
            personal.setImageName("1.png");
            //personal.setDescription(arrStr[9]);
            personal.setRegDate(Date.valueOf(LocalDate.parse(arrStr[10], formatter)));
            //personal.setImageName(arrStr[11]);
            entityManager.persist(personal);
            s_res = "1/" + "\r";
            //===============================Notification==========================
        } else {
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetPersonalInfo(String[] arrStr) {
        transaction.begin();
        PersonalInfoClass personalInfo = null;
        try {
            TypedQuery<PersonalEntity> query = entityManager.createQuery("SELECT e FROM PersonalEntity e where e.login =:login", PersonalEntity.class);
            query.setParameter("login", arrStr[1]);
            PersonalEntity resultPersonal = null;
            resultPersonal = query.getSingleResult();
            String infoS = resultPersonal.getLogin() + ">>" + resultPersonal.getPassword() + ">>" + resultPersonal.getNameSername() +
                    ">>" + resultPersonal.getContacts() + ">>" + resultPersonal.getEmail() + ">>" + resultPersonal.getRole() +
                    ">>" + resultPersonal.getSubrole() + ">>" + resultPersonal.getStatus() + ">>" + resultPersonal.getDescription() +
                    ">>" + resultPersonal.getRegDate() + "\r";
            BufferedImage image = ImageIO.read(new File("D:\\FCP\\SEM7\\CURS\\Project\\DataLib\\src\\main\\resources\\images\\" + resultPersonal.getImageName()));

            //BufferedImage image = ImageIO.read(new File("/images/" + resultPersonal.getImageName()));
            s_res = infoS;
            globalImage = image;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object UpdatePersonalInfo(String[] arrStr, BufferedImage image) {
        try {
            transaction.begin();
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TypedQuery<PersonalEntity> query = entityManager.createQuery("SELECT e FROM PersonalEntity e where e.login =:login", PersonalEntity.class);
            query.setParameter("login", arrStr[1]);
            PersonalEntity resultPersonal = null;
            resultPersonal = query.getSingleResult();
            //resultPersonal.setLogin(arrStr[11]);
            //resultPersonal.setPassword(arrStr[2]);
            //resultPersonal.setNameSername(arrStr[3]);
            resultPersonal.setContacts(arrStr[2]);
            resultPersonal.setEmail(arrStr[3]);
            //resultPersonal.setRole(arrStr[6]);
            //resultPersonal.setSubrole(arrStr[7]);
            //resultPersonal.setStatus(arrStr[8]);
            resultPersonal.setDescription(arrStr[4]);
            //resultPersonal.setRegDate(Date.valueOf(LocalDate.parse(arrStr[10], formatter)));
            Path path = Paths.get("D:\\FCP\\SEM7\\CURS\\Project\\DataLib\\src\\main\\resources\\images\\" + resultPersonal.getImageName());
            Files.delete(path);
            String newFileName = CreateImageName();
            resultPersonal.setImageName(newFileName);
            ImageIO.write(image, "PNG", new File("D:\\FCP\\SEM7\\CURS\\Project\\DataLib\\src\\main\\resources\\images\\" + newFileName));
            entityManager.merge(resultPersonal);
            s_res = "success" + "\r";
            //================================Notification==========================
        }
        catch (Exception e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }
    public Object GetPersonalImage() {
        return globalImage;
    }

    public List<BufferedImage> GetChatImageList() {
        return globalImageList;
    }

    public List<String> GetNameSernameList() {
        return globalNameSernameList;
    }

    public Object UpdatePersonalInfoAsManager(String[] arrStr) {
        try {
            transaction.begin();
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TypedQuery<PersonalEntity> query = entityManager.createQuery("SELECT e FROM PersonalEntity e where e.login =:login", PersonalEntity.class);
            query.setParameter("login", arrStr[2]);
            PersonalEntity resultPersonal = null;
            resultPersonal = query.getSingleResult();
            resultPersonal.setContacts(arrStr[3]);
            resultPersonal.setEmail(arrStr[4]);
            resultPersonal.setRole(arrStr[5]);
            resultPersonal.setSubrole(arrStr[6]);
            resultPersonal.setStatus(arrStr[7]);
            entityManager.merge(resultPersonal);
            s_res = "success" + "\r";
            //==============================Notification=====================
        }
        catch (Exception e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetClientsList(String arrStr[]) {
        try {
            s_res = "";
            transaction.begin();
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TypedQuery<ClientsEntity> query = entityManager.createQuery("SELECT e FROM ClientsEntity e", ClientsEntity.class);
            List<ClientsEntity> clientsList = new ArrayList<ClientsEntity>();
            clientsList = query.getResultList();
            for (ClientsEntity client : clientsList) {
                s_res += client.getName() + "<<" + client.getType() + "<<" + client.getPersonalByResponsableId().getNameSername() + "<<" + client.getClientsId();
                s_res += ">>";
            }
            s_res += "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
            throw new RuntimeException(e);
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetClientInfo(String arrStr[]) {
        try {
            transaction.begin();
            TypedQuery<ClientsEntity> query = entityManager.createQuery("SELECT e FROM ClientsEntity e WHERE e.clientsId =:id", ClientsEntity.class);
            query.setParameter("id", arrStr[1]);
            ClientsEntity client = new ClientsEntity();
            client = query.getSingleResult();
            s_res = client.getName() + "<<" + client.getPersonalByResponsableId().getNameSername() + "<<" + client.getPhone() +
                    "<<" + client.getEmail() + "<<" + client.getAdress() + "<<" + client.getDescription() +
                    "<<" + client.getType() + "<<" + client.getWork_type() + "<<"
                    + client.getReg_date().toString().split(" ")[0] + ">>";
            List<TasksEntity> tasksEntityList = (List<TasksEntity>) client.getTasksByClientsId();
            List<BusinessEntity> businessEntityList = (List<BusinessEntity>) client.getBusinessesByClientsId();
            List<ProcessesEntity> processesEntityList = (List<ProcessesEntity>) client.getProcessesByClientsId();
            List<JournalsEntity> journalsEntityList = (List<JournalsEntity>) client.getJournalsByClientsId();
            List<CommentsEntity> commentsEntityList = (List<CommentsEntity>) journalsEntityList.get(0).getCommentsByJournalsId();
            for(TasksEntity task: tasksEntityList) {
                s_res += task.getName() + "<<";
            }
            s_res += ">>";
            for(BusinessEntity business: businessEntityList) {
                s_res += business.getName() + "<<";
            }
            s_res += ">>";
            for(ProcessesEntity process: processesEntityList) {
                s_res += process.getProcessesId() + "<<";
            }
            s_res += ">>";
            for(CommentsEntity comment: commentsEntityList) {
                s_res += comment.getText() + "^^" + comment.getPersonalBySenderId().getNameSername() + "^^" + comment.getDate() + "^^" + comment.getPersonalBySenderId().getLogin() + "<<";
            }
            s_res += ">>";
            s_res += "\r";
        }
        catch (Exception e) {
            s_res = "null" + "\r";
            throw new RuntimeException(e);
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object AddClient(String[] arrStr) {
        transaction.begin();

        TypedQuery<PersonalEntity> queryP = entityManager.createQuery("SELECT e FROM PersonalEntity e where e.nameSername =:name", PersonalEntity.class);
        queryP.setParameter("name", arrStr[6]);
        PersonalEntity pers = queryP.getSingleResult();

        TypedQuery<ClientsEntity> query = entityManager.createQuery("SELECT e FROM ClientsEntity e where e.name =:name or e.email =:email", ClientsEntity.class);
        query.setParameter("name", arrStr[1]);
        query.setParameter("email", arrStr[2]);
        List<ClientsEntity> resultClients = null;
        resultClients = query.getResultList();

        if (resultClients.isEmpty()) {
            ClientsEntity client = new ClientsEntity();
            client.setName(arrStr[1]);
            client.setEmail(arrStr[2]);
            client.setPhone(arrStr[3]);
            client.setAdress(arrStr[4]);
            client.setDescription(arrStr[5]);
            client.setResponsableId(pers.getPersonalId());
            entityManager.persist(client);

            query = entityManager.createQuery("SELECT e FROM ClientsEntity e where e.name =:name and e.email =:email", ClientsEntity.class);
            query.setParameter("name", arrStr[1]);
            query.setParameter("email", arrStr[2]);
            JournalsEntity journal = new JournalsEntity();
            journal.setClientId(query.getSingleResult().getClientsId());
            entityManager.persist(journal);
            s_res = "1/" + "\r";
            //===============================Notification==========================
        } else {
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object UpdateClientInfo(String[] arrStr) {
        try {
            transaction.begin();
            System.out.println(arrStr[0]);
            TypedQuery<PersonalEntity> queryP = entityManager.createQuery("SELECT e FROM PersonalEntity e where e.nameSername =:name", PersonalEntity.class);
            queryP.setParameter("name", arrStr[6]);
            PersonalEntity pers = queryP.getSingleResult();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TypedQuery<ClientsEntity> query = entityManager.createQuery("SELECT e FROM ClientsEntity e where e.name =:name and e.email =:email", ClientsEntity.class);
            query.setParameter("name", arrStr[10]);
            query.setParameter("email", arrStr[11]);
            ClientsEntity resultClient = null;
            resultClient = query.getSingleResult();
            resultClient.setName(arrStr[1]);
            resultClient.setPhone(arrStr[2]);
            resultClient.setEmail(arrStr[3]);
            resultClient.setAdress(arrStr[4]);
            resultClient.setDescription(arrStr[5]);
            resultClient.setResponsableId(pers.getPersonalId());
            resultClient.setType(arrStr[7]);
            resultClient.setWork_type(arrStr[8]);
            java.util.Date date = new java.util.Date();
            if(arrStr[9].equals("null")) {
                date = null;
            }
            else {
                date = Date.valueOf(LocalDate.parse(arrStr[9], formatter));
            }
            resultClient.setReg_date(date);
            entityManager.merge(resultClient);
            s_res = "success" + "\r";
            //================================Notification==========================
        }
        catch (Exception e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetPaymentList(String[] arrStr) {
        try {
            s_res = "";
            transaction.begin();
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TypedQuery<PaymentsEntity> query = entityManager.createQuery("SELECT e FROM PaymentsEntity e", PaymentsEntity.class);
            List<PaymentsEntity> paymentsList = new ArrayList<PaymentsEntity>();
            paymentsList = query.getResultList();
            for (PaymentsEntity payment : paymentsList) {
                s_res += payment.getPaymentId() + "<<" + payment.getDeadline() + "<<" + payment.getFinalPrice() + "<<" + payment.getClientsByPaymenterId().getName() +
                "<<" + payment.getStatus() + "<<" + payment.getPaymenterId();
                s_res += ">>";
            }
            s_res += "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
            throw new RuntimeException(e);
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object AddPayment(String[] arrStr) {
        transaction.begin();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TypedQuery<ClientsEntity> query = entityManager.createQuery("SELECT e FROM ClientsEntity e where e.name =:name", ClientsEntity.class);
            query.setParameter("name", arrStr[4]);
            PaymentsEntity payment = new PaymentsEntity();
            payment.setCreationDate(Date.valueOf(LocalDate.parse(arrStr[1], formatter)));
            payment.setDeadline(Date.valueOf(LocalDate.parse(arrStr[2], formatter)));
            payment.setSubInfo(arrStr[3]);
            payment.setPaymenterId(query.getSingleResult().getClientsId());

            query = entityManager.createQuery("SELECT e FROM ClientsEntity e where e.name =:name", ClientsEntity.class);
            query.setParameter("name", arrStr[5]);

            payment.setRecieverId(query.getSingleResult().getClientsId());
            payment.setItemId(Integer.parseInt(arrStr[6]));
            payment.setAmount(Integer.parseInt(arrStr[7]));
            payment.setFinalPrice(Integer.parseInt(arrStr[8]));
            payment.setStatus("Создан");
            entityManager.persist(payment);

            s_res = "1/" + "\r";
        }
        catch (Exception e) {
            s_res = "null" + "\r";
            throw new RuntimeException(e);
        }
            //===============================Notification==========================
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object UpdatePayment(String arrStr[]) {
        transaction.begin();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TypedQuery<ClientsEntity> query = entityManager.createQuery("SELECT e FROM ClientsEntity e where e.name =:name", ClientsEntity.class);
            query.setParameter("name", arrStr[4]);
            TypedQuery<PaymentsEntity> query2 = entityManager.createQuery("SELECT e from PaymentsEntity e where e.paymentId =:id", PaymentsEntity.class);
            query2.setParameter("id", Integer.parseInt(arrStr[9]));
            PaymentsEntity payment = query2.getSingleResult();
            if(payment.getStatus().equals("Закрыт") || payment.getStatus().equals("Отменен"))
            {
                s_res = null + "\r";
            }
            else {
                payment.setCreationDate(Date.valueOf(LocalDate.parse(arrStr[1], formatter)));
                payment.setDeadline(Date.valueOf(LocalDate.parse(arrStr[2], formatter)));
                payment.setSubInfo(arrStr[3]);
                payment.setPaymenterId(query.getSingleResult().getClientsId());

                query = entityManager.createQuery("SELECT e FROM ClientsEntity e where e.name =:name", ClientsEntity.class);
                query.setParameter("name", arrStr[5]);

                payment.setRecieverId(query.getSingleResult().getClientsId());
                payment.setItemId(Integer.parseInt(arrStr[6]));
                payment.setAmount(Integer.parseInt(arrStr[7]));
                payment.setFinalPrice(Integer.parseInt(arrStr[8]));
                entityManager.merge(payment);

                s_res = "1/" + "\r";
            }
        }
        catch (Exception e) {
            s_res = "null" + "\r";
            throw new RuntimeException(e);
        }
        //===============================Notification==========================
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetItemsList(String[] arrStr) {
        try {
            s_res = "";
            transaction.begin();
            TypedQuery<ItemsEntity> query = entityManager.createQuery("SELECT e FROM ItemsEntity e", ItemsEntity.class);
            List<ItemsEntity> itemsList = new ArrayList<>();
            itemsList = query.getResultList();
            for (ItemsEntity item : itemsList) {
                s_res += item.getName() + "<<" + item.getArticul() + "<<" + item.getPrice() + "<<" + item.getTaxes()
                        + "<<" + item.getMeasurement() + "<<" + item.getItemId();
                s_res += ">>";
            }
            s_res += "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
            throw new RuntimeException(e);
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object AddItem(String[] arrStr) {
        transaction.begin();

        TypedQuery<ItemsEntity> query = entityManager.createQuery("SELECT e FROM ItemsEntity e where e.name =:name", ItemsEntity.class);
        query.setParameter("name", arrStr[1]);
        List<ItemsEntity> resultItems = null;
        resultItems = query.getResultList();

        if (resultItems.isEmpty()) {
            ItemsEntity item = new ItemsEntity();
            item.setName(arrStr[1]);
            item.setArticul(arrStr[2]);
            item.setPrice(Integer.parseInt(arrStr[3]));
            item.setTaxes(Integer.parseInt(arrStr[4]));
            item.setMeasurement(arrStr[5]);
            entityManager.persist(item);
            s_res = "1/" + "\r";
            //===============================Notification==========================
        } else {
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetPaymentInfo(String[] arrStr) {
        try {
            s_res = "";
            transaction.begin();
            TypedQuery<PaymentsEntity> query = entityManager.createQuery("SELECT e FROM PaymentsEntity e where e.paymentId =:id", PaymentsEntity.class);
            query.setParameter("id", arrStr[1]);
            PaymentsEntity payment = query.getSingleResult();
            s_res = payment.getCreationDate() + ">>" + payment.getClientsByPaymenterId().getName() + ">>"
                    + payment.getClientsByRecieverId().getName() + ">>" + payment.getItemsByItemId().getItemId() + "<<"
                    + payment.getItemsByItemId().getName() + "<<" + payment.getItemsByItemId().getMeasurement() + "<<"
                    + payment.getAmount() + "<<" + payment.getItemsByItemId().getPrice() + "<<"
                    + payment.getItemsByItemId().getTaxes() + "<<"
                    + (payment.getFinalPrice()) + ">>";
            s_res += "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
            throw new RuntimeException(e);
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetFullPaymentInfo(String[] arrStr) {
        try {
            s_res = "";
            transaction.begin();
            TypedQuery<PaymentsEntity> query = entityManager.createQuery("SELECT e FROM PaymentsEntity e where e.paymentId =:id", PaymentsEntity.class);
            query.setParameter("id", arrStr[1]);
            PaymentsEntity payment = query.getSingleResult();
            s_res = payment.getCreationDate().toString() + ">>" + payment.getClientsByPaymenterId().getName() + ">>"
                    + payment.getClientsByRecieverId().getName() + ">>" + payment.getDeadline().toString() + ">>"
                    + payment.getSubInfo() + ">>" + payment.getItemsByItemId().getName() + ">>" + payment.getAmount() + ">>"
                    + payment.getItemsByItemId().getMeasurement() + ">>" + payment.getFinalPrice() + ">>"
                    + payment.getItemsByItemId().getTaxes() + ">>" + payment.getPaymentId();
            s_res += "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
            throw new RuntimeException(e);
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetTasksList(String[] arrStr) {
        s_res = "";
        transaction.begin();
        TypedQuery<TasksEntity> q = entityManager.createQuery("SELECT e from TasksEntity e", TasksEntity.class);
        try {
            List<TasksEntity> list = q.getResultList();
            for (TasksEntity task : list) {
                TypedQuery<BusinessEntity> q2 = entityManager.createQuery("SELECT e from BusinessEntity e where " +
                        "e.taskID =:id", BusinessEntity.class);
                q2.setParameter("id", task.getTasksId());
                List<BusinessEntity> businessEntities = q2.getResultList();
                int completeBCount = 0;
                for(BusinessEntity business: businessEntities) {
                    if (!business.getStatus().equals("Активно")) {
                        completeBCount++;
                    }
                }
                s_res += task.getName() + "<<" + (completeBCount + "/" + businessEntities.size()) + "<<"
                        + task.getCreationDate() + "<<" + task.getDeadline() + "<<"
                        + task.getPersonalByResponsableId().getNameSername() + "<<"
                        + task.getPersonalByCheckerId().getNameSername() + "<<" + task.getStatus() + "<<" + task.getTasksId();
                s_res += ">>";
            }
            s_res += "\r";
        } catch (Exception e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetPersonalObeyList(String[] arrStr) {
        s_res = "";
        transaction.begin();
        TypedQuery<PersonalEntity> q = entityManager.createQuery("SELECT e from PersonalEntity e where e.role = 'obey'", PersonalEntity.class);
        try {
            List<PersonalEntity> list = q.getResultList();
            for (PersonalEntity personal : list) {
                s_res += personal.getNameSername() + ">>" + personal.getContacts() + ">>" + personal.getEmail() + ">>" + personal.getSubrole() + ">>" + personal.getStatus() + ">>" + personal.getLogin() + ">>" + personal.getPassword();
                s_res += "<<";
            }
            s_res += "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetPersonalControlList(String[] arrStr) {
        s_res = "";
        transaction.begin();
        TypedQuery<PersonalEntity> q = entityManager.createQuery("SELECT e from PersonalEntity e where e.role = 'control'", PersonalEntity.class);
        try {
            List<PersonalEntity> list = q.getResultList();
            for (PersonalEntity personal : list) {
                s_res += personal.getNameSername() + ">>" + personal.getContacts() + ">>" + personal.getEmail() + ">>" + personal.getSubrole() + ">>" + personal.getStatus() + ">>" + personal.getLogin() + ">>" + personal.getPassword();
                s_res += "<<";
            }
            s_res += "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object AddTask(String[] arrStr) {
        transaction.begin();

        TypedQuery<TasksEntity> queryT = entityManager.createQuery("SELECT e FROM TasksEntity e where e.name =:name " +
                "and e.personalByResponsableId.nameSername =:responsableName", TasksEntity.class);
        queryT.setParameter("name", arrStr[1]);
        queryT.setParameter("responsableName", arrStr[2]);
        List<TasksEntity> checkTasks = queryT.getResultList();

        if (checkTasks.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TasksEntity task = new TasksEntity();
            //==============================Data Get==============================
            TypedQuery<PersonalEntity> queryPers = entityManager.createQuery("SELECT e FROM PersonalEntity e", PersonalEntity.class);
            List<PersonalEntity> personalList = queryPers.getResultList();
            int responsable_id = 0, checker_id = 0;
            for(PersonalEntity personal: personalList) {
                if(personal.getNameSername().equals(arrStr[2])) {
                    responsable_id = personal.getPersonalId();
                }
                else if (personal.getNameSername().equals(arrStr[4])) {
                    checker_id = personal.getPersonalId();
                }
            }
            if(!arrStr[6].equals("Не выбран")) {
                TypedQuery<ProjectsEntity> queryProj = entityManager.createQuery("SELECT e FROM ProjectsEntity e where " +
                        "e.name =:name", ProjectsEntity.class);
                queryProj.setParameter("name", arrStr[6]);
                task.setProjectId(queryProj.getSingleResult().getProjectsId());
            }
            if(!arrStr[7].equals("выбран")) {
                System.out.println(arrStr[7]);
                TypedQuery<ProcessesEntity> queryProc = entityManager.createQuery("SELECT e FROM ProcessesEntity e where " +
                        "e.processesId =:id", ProcessesEntity.class);
                queryProc.setParameter("id", Integer.parseInt(arrStr[7]));
                task.setProcessId(queryProc.getSingleResult().getProcessesId());
            }
            if(!arrStr[8].equals("Не выбран")) {
                TypedQuery<ClientsEntity> queryC = entityManager.createQuery("SELECT e FROM ClientsEntity e where " +
                        "e.name =:name", ClientsEntity.class);
                queryC.setParameter("name", arrStr[8]);
                task.setClientId(queryC.getSingleResult().getClientsId());
            }
            //=============================/Data Get==============================
            task.setName(arrStr[1]);
            System.out.println(responsable_id);
            task.setResponsableId(responsable_id);
            task.setDescription(arrStr[3]);
            task.setCheckerId(checker_id);
            task.setDeadline(Date.valueOf(LocalDate.parse(arrStr[5], formatter2)));
            task.setStatus("Назначена");
            task.setCreationDate(Date.valueOf(LocalDate.parse(arrStr[9], formatter)));
            entityManager.persist(task);

            queryT = entityManager.createQuery("SELECT e FROM TasksEntity e where e.name =:name" +
                    " and e.responsableId =:id", TasksEntity.class);
            queryT.setParameter("name", arrStr[1]);
            queryT.setParameter("id", responsable_id);
            JournalsEntity journal = new JournalsEntity();
            journal.setTaskId(queryT.getSingleResult().getTasksId());
            entityManager.persist(journal);
            s_res = "1/" + "\r";
            //===============================Notification==========================
        } else {
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetProjectList(String[] arrStr) {
        try {
            s_res = "";
            transaction.begin();
            TypedQuery<ProjectsEntity> query = entityManager.createQuery("SELECT e FROM ProjectsEntity e", ProjectsEntity.class);
            List<ProjectsEntity> projectsList = new ArrayList<>();
            projectsList = query.getResultList();
            for (ProjectsEntity project : projectsList) {
                s_res += project.getName() + "<<" + project.getDescription() + "<<" + project.getResponsableId() + "<<"
                        + project.getDeadline() + "<<" + project.getStatus();
                s_res += ">>";
            }
            s_res += "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
            throw new RuntimeException(e);
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetProcessList(String[] arrStr) {
        try {
            s_res = "";
            transaction.begin();
            TypedQuery<ProcessesEntity> query = entityManager.createQuery("SELECT e FROM ProcessesEntity e", ProcessesEntity.class);
            List<ProcessesEntity> processesList = new ArrayList<>();
            processesList = query.getResultList();
            for (ProcessesEntity process : processesList) {
                s_res += process.getProcessesId() + "<<" + process.getStatus() + "<<" + process.getClientId() + "<<"
                        + process.getResponsableId() + "<<" + process.getCheckerId() + "<<" + process.getPayment() + "<<"
                        + process.getDescription() + "<<" + process.getProjectId();
                s_res += ">>";
            }
            s_res += "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
            e.printStackTrace();
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetTaskInfo(String[] arrStr) {
        try {
            transaction.begin();
            TypedQuery<TasksEntity> query = entityManager.createQuery("SELECT e FROM TasksEntity e WHERE e.name =:name and " +
                    "e.personalByResponsableId.nameSername =:responsableName", TasksEntity.class);
            query.setParameter("name", arrStr[1]);
            query.setParameter("responsableName", arrStr[2]);
            TasksEntity task = new TasksEntity();
            task = query.getSingleResult();
            s_res = task.getName() + "<<" + task.getPersonalByResponsableId().getNameSername() + "<<" + task.getDescription() +
                    "<<" + task.getPersonalByCheckerId().getNameSername() + "<<" + task.getDeadline().toString().split(" ")[0] +
                    "<<" + task.getStatus() + "<<" + task.getCreationDate().toString().split(" ")[0] +
                    "<<" + task.getPriority() + ">>";
            List<BusinessEntity> businessEntityList = (List<BusinessEntity>) task.getBusinessesByTasksId();
            List<JournalsEntity> journalsEntityList = (List<JournalsEntity>) task.getJournalsByTasksId();
            List<CommentsEntity> commentsEntityList = (List<CommentsEntity>) journalsEntityList.get(0).getCommentsByJournalsId();

            for(BusinessEntity business: businessEntityList) {
                s_res += business.getName() + "<<";
            }
            s_res += ">>";
            for(CommentsEntity comment: commentsEntityList) {
                s_res += comment.getText() + "^^" + comment.getPersonalBySenderId().getNameSername() + "^^" + comment.getDate() + "^^" + comment.getPersonalBySenderId().getLogin() + "<<";
            }
            s_res += ">>";
            s_res += "\r";
        }
        catch (Exception e) {
            s_res = "null" + "\r";
            throw new RuntimeException(e);
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object UpdateTaskInfo(String[] arrStr) {
        try {
            transaction.begin();
            System.out.println(arrStr[0]);
            TypedQuery<PersonalEntity> queryP = entityManager.createQuery("SELECT e FROM PersonalEntity e", PersonalEntity.class);
            List<PersonalEntity> pers = queryP.getResultList();
            int responsable_id = 0, checker_id = 0;
            for(PersonalEntity personal: pers) {
                if(personal.getNameSername().equals(arrStr[2])) {
                    responsable_id = personal.getPersonalId();
                }
                else if (personal.getNameSername().equals(arrStr[4])) {
                    checker_id = personal.getPersonalId();
                }
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TypedQuery<TasksEntity> query = entityManager.createQuery("SELECT e FROM TasksEntity e where e.name =:name and" +
                    " e.personalByResponsableId.nameSername =:responsableName", TasksEntity.class);
            query.setParameter("name", arrStr[7]);
            query.setParameter("responsableName", arrStr[8]);
            TasksEntity resultTask = null;
            resultTask = query.getSingleResult();
            resultTask.setName(arrStr[1]);
            resultTask.setResponsableId(responsable_id);
            resultTask.setDescription(arrStr[3]);
            resultTask.setCheckerId(checker_id);
            resultTask.setPriority(arrStr[6]);
            java.util.Date date = new java.util.Date();
            if(arrStr[5].equals("null")) {
                date = null;
            }
            else {
                date = Date.valueOf(LocalDate.parse(arrStr[5], formatter));
            }
            resultTask.setDeadline((Date) date);
            entityManager.merge(resultTask);
            s_res = "success" + "\r";
            //================================Notification==========================
        }
        catch (Exception e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetChatsList(String[] arrStr) {
        try {
            globalImageList = new ArrayList<>();
            s_res = "";
            transaction.begin();
            TypedQuery<ChatsEntity> query = entityManager.createQuery("SELECT e FROM ChatsEntity e" +
                    " inner join ChatMembersEntity b on b.chatId = e.chatsId" +
                    " where b.personalByPersonalId.nameSername =: nameSername", ChatsEntity.class);
            query.setParameter("nameSername", arrStr[1]);
            List<ChatsEntity> chatsList = new ArrayList<>();
            chatsList = query.getResultList();
            System.out.println(chatsList.get(0).getName());
            for (ChatsEntity chat : chatsList) {
                Iterator<MessagesEntity> iterator = chat.getMessagesByChatsId().iterator();
                MessagesEntity lastMessage = new MessagesEntity();
                while (iterator.hasNext()) {
                    lastMessage = iterator.next();
                }
                String senderName = " ", sendTime = " ", sendText = " ";
                try {
                    senderName = lastMessage.getPersonalBySenderId().getNameSername();
                    sendTime = lastMessage.getTime().toString();
                    sendText = lastMessage.getText();
                }
                catch (Exception e) {
                    System.out.println(e);
                    senderName = " ";
                }
                try {
                    //BufferedImage image = ImageIO.read(new File("/resources/images/" + chat.getImageName()));
                    BufferedImage image = ImageIO.read(new File("D:\\FCP\\SEM7\\CURS\\Project\\DataLib\\src\\main\\resources\\images\\" + chat.getImageName()));
                    globalImageList.add(image);
                }
                catch (Exception e) {
                    System.out.println(e);
                    //BufferedImage image = ImageIO.read(new File("/images/1.png"));
                    BufferedImage image = ImageIO.read(new File("D:\\FCP\\SEM7\\CURS\\Project\\DataLib\\src\\main\\resources\\images\\1.png"));
                    globalImageList.add(image);
                }

                TypedQuery<MessageStatusEntity> query2 = entityManager.createQuery("SELECT COUNT(e)" +
                        " from MessageStatusEntity e where e.personalByPersonalId.nameSername =:personalId and e.chatId =:chatId" +
                                " and e.status = 'Не прочитано'", MessageStatusEntity.class);

                query2.setParameter("personalId", arrStr[1]);
                query2.setParameter("chatId", chat.getChatsId());

                s_res += chat.getName() + "<<" + senderName + "<<" + sendText + "<<" + sendTime
                        + "<<" + query2.getSingleResult();
                s_res += ">>";
            }
            s_res += "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
            e.printStackTrace();
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object AddChat(String[] arrStr, BufferedImage image) {
        try {
            transaction.begin();
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            TypedQuery<ChatsEntity> query = entityManager.createQuery("SELECT e FROM ChatsEntity e where e.name =:name", ChatsEntity.class);
            query.setParameter("name", arrStr[1]);
            List<ChatsEntity> resultChat = null;
            resultChat = query.getResultList();
            if(resultChat.isEmpty()) {
                ChatsEntity chat = new ChatsEntity();
                chat.setName(arrStr[1]);
                chat.setDescription(arrStr[2]);

                String newFileName = CreateImageName();
                chat.setImageName(newFileName);
                ImageIO.write(image, "PNG", new File("D:\\FCP\\SEM7\\CURS\\Project\\DataLib\\src\\main\\resources\\images\\" + newFileName));
                entityManager.persist(chat);

                query = entityManager.createQuery("SELECT e FROM ChatsEntity e where e.name =:name", ChatsEntity.class);
                query.setParameter("name", arrStr[1]);

                List<Integer> personalIdArr = new ArrayList<>();

                for(int i = 3; i < arrStr.length - 1; i++) {
                    TypedQuery<PersonalEntity> query2 = entityManager.createQuery("SELECT e FROM PersonalEntity e" +
                            " where e.nameSername =:nameSername", PersonalEntity.class);
                    query2.setParameter("nameSername", arrStr[i]);
                    ChatMembersEntity entity = new ChatMembersEntity();
                    entity.setChatId(query.getSingleResult().getChatsId());
                    entity.setPersonalId(query2.getSingleResult().getPersonalId());
                    entityManager.persist(entity);
                }
                //resultPersonal.setRegDate(Date.valueOf(LocalDate.parse(arrStr[10], formatter)));

                s_res = "success" + "\r";
            }
            else {
                s_res = "null" + "\r";
            }
            //================================Notification==========================
        }
        catch (Exception e) {
            e.printStackTrace();
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetChatMessages(String[] arrStr) {
        try {

            globalImageList = new ArrayList<>();
            globalNameSernameList = new ArrayList<>();
            s_res = "";
            transaction.begin();
            TypedQuery<ChatsEntity> queryChat = entityManager.createQuery("SELECT e FROM ChatsEntity e" +
                    " where e.name =:name", ChatsEntity.class);
            queryChat.setParameter("name", arrStr[1]);

            //=====================================================================
            TypedQuery<ChatMembersEntity> queryMembers = entityManager.createQuery("SELECT e FROM ChatMembersEntity e " +
                    "where e.chatId =:chatId", ChatMembersEntity.class);
            queryMembers.setParameter("chatId", queryChat.getSingleResult().getChatsId());
            List<ChatMembersEntity> membersList = queryMembers.getResultList();
            for(ChatMembersEntity member: membersList) {
                try {
                    //BufferedImage image = ImageIO.read(new File("/resources/images/" + chat.getImageName()));
                    BufferedImage image = ImageIO.read(new File(
                            "D:\\FCP\\SEM7\\CURS\\Project\\DataLib\\src\\main\\resources\\images\\"
                                    + member.getPersonalByPersonalId().getImageName()));
                    globalImageList.add(image);
                    globalNameSernameList.add(member.getPersonalByPersonalId().getNameSername());
                } catch (Exception e) {
                    e.printStackTrace();
                    //BufferedImage image = ImageIO.read(new File("/images/1.png"));
                    BufferedImage image = ImageIO.read(new File(
                            "D:\\FCP\\SEM7\\CURS\\Project\\DataLib\\src\\main\\resources\\images\\1.png"));
                    globalImageList.add(image);
                    globalNameSernameList.add(member.getPersonalByPersonalId().getNameSername());
                }
            }
            //=====================================================================

            TypedQuery<MessagesEntity> query = entityManager.createQuery("SELECT e FROM MessagesEntity e" +
                    " where e.chatId =:id", MessagesEntity.class);
            query.setParameter("id", queryChat.getSingleResult().getChatsId());
            System.out.println(queryChat.getSingleResult().getChatsId() + " eeeeeeeeeeeeeeeeee");
            List<MessagesEntity> messagesList;
            messagesList = query.getResultList();
            String dateChecker = "0";
            s_res += globalNameSernameList.size();
            for (MessagesEntity message : messagesList) {
                if(!message.getDate().toString().equals(dateChecker)) {
                    s_res += "^^" + message.getDate().toString() + ">>";
                    dateChecker = message.getDate().toString();
                }
                s_res += message.getText() + "<<" + message.getTime() + "<<" + message.getPersonalBySenderId().getNameSername();
                s_res += ">>";
            }
        } catch (Exception e) {
            e.printStackTrace();
            s_res = "null" + "\r";
            //throw new RuntimeException(e);
        }
        transaction.commit();
        entityManager.clear();
        s_res += "\r";
        return s_res;
    }

    public Object GetChatMessagesNames(String[] arrStr) {
        s_res = "";
        for(String i: globalNameSernameList) {
            s_res += i + ">>";
        }
        s_res += "\r";
        return s_res;
    }

    public Object AddMessage(String[] arrStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            s_res = "";
            transaction.begin();


            TypedQuery<ChatsEntity> query = entityManager.createQuery("SELECT e FROM ChatsEntity e where e.name =:name", ChatsEntity.class);
            query.setParameter("name", arrStr[4]);
            ChatsEntity chat = query.getSingleResult();
            TypedQuery<PersonalEntity> queryP = entityManager.createQuery("SELECT e FROM PersonalEntity e where e.nameSername =:name", PersonalEntity.class);
            queryP.setParameter("name", arrStr[3]);
            MessagesEntity message = new MessagesEntity();
            message.setText(arrStr[1]);
            message.setDate(Date.valueOf(LocalDate.parse(arrStr[2], formatter)));
            message.setSenderId(queryP.getSingleResult().getPersonalId());
            message.setChatId(chat.getChatsId());
            message.setTime(Time.valueOf(arrStr[5]));
            entityManager.persist(message);
            entityManager.clear();
            s_res = "success" + "\r";

            TypedQuery<MessagesEntity> queryM = entityManager.createQuery("SELECT e FROM MessagesEntity e order by e.messagesId desc", MessagesEntity.class);
            List<MessagesEntity> queryMResult = queryM.getResultList();

            TypedQuery<ChatMembersEntity> queryCM = entityManager.createQuery("SELECT e FROM ChatMembersEntity e where e.chatId =:chatID" +
                    " and e.personalByPersonalId.nameSername <>:nameSername", ChatMembersEntity.class);
            queryCM.setParameter("nameSername", arrStr[3]);
            queryCM.setParameter("chatID", chat.getChatsId());
            System.out.println(chat.getChatsId());
            List<ChatMembersEntity> chatMembers = queryCM.getResultList();
            for(ChatMembersEntity member: chatMembers) {
                MessageStatusEntity mesStatus = new MessageStatusEntity();
                mesStatus.setPersonalId(member.getPersonalId());
                mesStatus.setMessageId(queryMResult.get(0).getMessagesId());
                mesStatus.setChatId(chat.getChatsId());
                mesStatus.setStatus("Не прочитано");
                entityManager.persist(mesStatus);
            }
        } catch (Exception e) {
            s_res = "null" + "\r";
            e.printStackTrace();
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object DeleteMessageStatus(String[] arrStr) {
        try {
            s_res = "";
            transaction.begin();
            TypedQuery<MessageStatusEntity> query = entityManager.createQuery("SELECT e FROM MessageStatusEntity e" +
                    " WHERE e.chatByChatId.name = :chatName" +
                    " and e.personalByPersonalId.nameSername =:nameSername", MessageStatusEntity.class);
            query.setParameter("chatName", arrStr[1]);
            query.setParameter("nameSername", arrStr[2]);
            List<MessageStatusEntity> statusesList = query.getResultList();
            for(MessageStatusEntity status: statusesList) {
                entityManager.remove(status);
            }
           s_res = "succes" + "\r";
        } catch (Exception e) {
            s_res = "null" + "\r";
            e.printStackTrace();
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetProjectsList(String[] arrStr) {
        s_res = "";
        transaction.begin();
        TypedQuery<ProjectsEntity> q = entityManager.createQuery("SELECT e from ProjectsEntity e", ProjectsEntity.class);
        try {
            List<ProjectsEntity> list = q.getResultList();
            for (ProjectsEntity project : list) {
                TypedQuery<TasksEntity> q2 = entityManager.createQuery("SELECT e from TasksEntity e where " +
                        "e.projectId =:id", TasksEntity.class);
                q2.setParameter("id", project.getProjectsId());
                List<TasksEntity> tasksEntities = q2.getResultList();
                int completeBCount = 0;
                for(TasksEntity task: tasksEntities) {
                    if (!task.getStatus().equals("Назначена")) {
                        completeBCount++;
                    }
                }
                s_res += project.getName() + "<<" + (completeBCount + "/" + tasksEntities.size()) + "<<"
                        + project.getCreationDate() + "<<" + project.getDeadline().toString() + "<<"
                        + project.getProjectMembersByProjectsId().iterator().next().getTeamName() + "<<"
                        + project.getPersonalByCheckerId().getNameSername() + "<<" + project.getStatus() + "<<" + project.getProjectsId();
                s_res += ">>";
            }
            s_res += "\r";
        } catch (Exception e) {
            e.printStackTrace();
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object AddProject(String[] arrStr) {
        transaction.begin();

        TypedQuery<ProjectsEntity> queryT = entityManager.createQuery("SELECT e FROM ProjectsEntity e where e.name =:name",
                ProjectsEntity.class);
        queryT.setParameter("name", arrStr[1]);
        List<ProjectsEntity> checkProjects = queryT.getResultList();

        if (checkProjects.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            ProjectsEntity project = new ProjectsEntity();
            //==============================Data Get==============================
            TypedQuery<PersonalEntity> queryPers = entityManager.createQuery("SELECT e FROM PersonalEntity e", PersonalEntity.class);
            List<PersonalEntity> personalList = queryPers.getResultList();
            List<Integer> responsable_ids = new ArrayList<>();
            String[] recieved_names = arrStr[2].split(">>");
                int checker_id = 0;
                for (PersonalEntity personal : personalList) {
                    if(!arrStr[2].equals("Команда существует")) {
                        for (String i : recieved_names) {
                            if (personal.getNameSername().equals(i)) {
                                responsable_ids.add(personal.getPersonalId());
                            }
                        }
                    }
                    if (personal.getNameSername().equals(arrStr[4])) {
                        checker_id = personal.getPersonalId();
                    }
                }
            //=============================/Data Get==============================
            project.setName(arrStr[1]);
            project.setDescription(arrStr[3]);
            project.setCheckerId(checker_id);
            project.setDeadline(Date.valueOf(LocalDate.parse(arrStr[5], formatter)));
            project.setStatus("Активен");
            project.setCreationDate(Date.valueOf(LocalDate.now()));
            entityManager.persist(project);

            queryT = entityManager.createQuery("SELECT e FROM ProjectsEntity e where e.name =:name",
                    ProjectsEntity.class);
            queryT.setParameter("name", arrStr[1]);
            JournalsEntity journal = new JournalsEntity();
            journal.setProjectId(queryT.getSingleResult().getProjectsId());
            entityManager.persist(journal);

            if(arrStr[2].equals("Команда существует")) {
                TypedQuery<ProjectMembersEntity> queryPM = entityManager.createQuery("SELECT e FROM ProjectMembersEntity e" +
                        " WHERE e.teamName =:teamName", ProjectMembersEntity.class);
                queryPM.setParameter("teamName", arrStr[6]);
                List<ProjectMembersEntity> mem = queryPM.getResultList();
                for(ProjectMembersEntity me: mem) {
                    ProjectMembersEntity m = me;
                    m.setProjectId(queryT.getSingleResult().getProjectsId());
                    entityManager.persist(m);
                }
            }
            else {
                for (Integer i : responsable_ids) {
                    ProjectMembersEntity member = new ProjectMembersEntity();
                    member.setProjectId(queryT.getSingleResult().getProjectsId());
                    member.setPersonalId(i);
                    member.setTeamName(arrStr[6]);
                    entityManager.persist(member);
                }
            }
            s_res = "1/" + "\r";
            //===============================Notification==========================
        } else {
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object GetTeamsList(String[] arrStr) {
        s_res = "";
        transaction.begin();
        TypedQuery<ProjectMembersEntity> q = entityManager.createQuery("SELECT distinct e.teamName from ProjectMembersEntity e",
                ProjectMembersEntity.class);
        try {
            List<String> list = Collections.singletonList(q.getResultList().toString());
            for (String i : list) {
                i.replace("\\[", "");
                i.replace("]", "");
                System.out.println(i + " ooooooooooooooooooooo");
                s_res += i + ">>";
            }
            s_res += "\r";
        } catch (Exception e) {
            e.printStackTrace();
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }

    public Object DeletePersonal(String[] arrStr) {
        s_res = "";
        transaction.begin();
        try {
            TypedQuery<PersonalEntity> query = entityManager.createQuery("SELECT e FROM PersonalEntity e" +
                    " where e.nameSername =:nameSername", PersonalEntity.class);
            query.setParameter("nameSername", arrStr[1]);
            PersonalEntity personal = query.getSingleResult();
            entityManager.remove(personal);
            s_res = "success" + "\r";
        } catch (Exception e) {
            e.printStackTrace();
            s_res = "null" + "\r";
        }
        transaction.commit();
        entityManager.clear();
        return s_res;
    }
    //==============================================================================================
    //==============================================================================================
    //==============================================================================================
    public Object AddSeries(String[] arrStr) {
        try {
            String sq_str_check = "Select * FROM belaz";
            ResultSet res = sq.executeQuery(sq_str_check);
            boolean SeriesCheck = true;

            while (res.next()) {
                if (res.getString("Series").equals(arrStr[1])) {
                    SeriesCheck = false;
                    break;
                }
            }
            if (SeriesCheck) {
                sq_str_check = "INSERT INTO belaz(Series, Model, imageM) VALUES ('" + arrStr[1] + "', '" + arrStr[2] + "', '" + arrStr[3] + "')";
                sq.executeUpdate(sq_str_check);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object UpdateSeriesPage(String[] arrStr) {
        try {
            x = new StringBuffer();
            String sq_str = "SELECT Series FROM belaz";
            ResultSet res = sq.executeQuery(sq_str);
            while (res.next()) {
                x.append(res.getString("Series") + ">>");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return x.toString() + "\r";
    }

    public Object UpdateModelPage(String[] arrStr) {
        try {
            x = new StringBuffer();
            String sq_str = "SELECT Model, imageM FROM belaz WHERE SERIES = '" + arrStr[1] + "'";
            ResultSet res = sq.executeQuery(sq_str);
            while (res.next()) {
                x.append(res.getString("Model") + ">>" + res.getString("imageM") + ">>");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return x.toString() + "\r";
    }

    public Object AddModel(String[] arrStr) {
        try {
            String sq_str_check = "Select Model FROM belaz WHERE Series = '" + arrStr[1] + "'";
            ResultSet res = sq.executeQuery(sq_str_check);
            boolean ModelCheck = true;

            while (res.next()) {
                if (res.getString("Model").equals(arrStr[2])) {
                    ModelCheck = false;
                    break;
                }
            }
            if (ModelCheck) {
                sq_str_check = "INSERT INTO belaz(Series, Model, imageM) VALUES ('" + arrStr[1] + "', '" + arrStr[2] + "', '" + arrStr[3] + "')";
                sq.executeUpdate(sq_str_check);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object AddMalCode(String[] arrStr) {
        try {
            String sq_str_check = "Select * FROM malfunction_code inner join belaz_malfunction_code ON belaz_malfunction_code.malfunction_code_id = malfunction_code.id INNER JOIN belaz ON belaz.id = belaz_malfunction_code.belaz_id WHERE belaz.Series = '" + arrStr[4] + "' AND belaz.Model = '" + arrStr[5] + "'";
            ResultSet res = sq.executeQuery(sq_str_check);
            boolean CodeCheck = true;
            while (res.next()) {
                if (res.getString("code").equals(arrStr[1]) || res.getString("full_code").equals(arrStr[2])) {
                    CodeCheck = false;
                    s_res = "null" + "\r";
                    break;
                }
            }
            if (CodeCheck) {
                sq_str_check = "Select systems.Text FROM systems INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[4] + "' AND belaz.Model = '" + arrStr[5] + "'";
                res = sq.executeQuery(sq_str_check);
                boolean SeriesCheck = false;

                while (res.next()) {
                    if (res.getString("systems.Text").equals(arrStr[3])) {
                        SeriesCheck = true;
                        break;
                    }
                }
                if (!SeriesCheck) {
                    sq_str_check = "INSERT INTO systems(Text) VALUES ('" + arrStr[3] + "')";
                    sq.executeUpdate(sq_str_check);
                }
                sq_str_check = "SELECT id FROM belaz WHERE Series = '" + arrStr[4] + "' AND Model = '" + arrStr[5] + "'";
                res = sq.executeQuery(sq_str_check);
                res.next();
                String belaz_id = res.getString("id");
                sq_str_check = "SELECT id FROM systems WHERE Text = '" + arrStr[3] + "'";
                res = sq.executeQuery(sq_str_check);
                res.next();
                String sys_id = res.getString("id");
                sq_str_check = "SELECT id FROM belaz_systems WHERE systems_id = " + sys_id + " AND belaz_id = " + belaz_id;
                res = sq.executeQuery(sq_str_check);
                if (!res.next()) {
                    sq_str_check = "INSERT INTO belaz_systems(belaz_id, systems_id) VALUES (" + belaz_id + ", " + sys_id + ")";
                    sq.executeUpdate(sq_str_check);
                }
                sq_str_check = "INSERT INTO malfunction_code(systems_id, code, full_code) VALUES (" + sys_id + ", '" + arrStr[1] + "', '" + arrStr[2] + "')";
                sq.executeUpdate(sq_str_check);
                sq_str_check = "SELECT id FROM malfunction_code WHERE code = '" + arrStr[1] + "' AND full_code = '" + arrStr[2] + "'";
                res = sq.executeQuery(sq_str_check);
                res.next();
                sq_str_check = "INSERT INTO belaz_malfunction_code(belaz_id, malfunction_code_id) VALUES (" + belaz_id + ", " + res.getString("id") + ")";
                sq.executeUpdate(sq_str_check);
                s_res = "1" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object UpdateMalCode(String[] arrStr) {
        try {
            String sq_str_check = "Select * FROM malfunction_code inner join belaz_malfunction_code ON belaz_malfunction_code.malfunction_code_id = malfunction_code.id INNER JOIN belaz ON belaz.id = belaz_malfunction_code.belaz_id WHERE belaz.Series = '" + arrStr[5] + "' AND belaz.Model = '" + arrStr[6] + "' AND NOT malfunction_code.code = '" + arrStr[1] + "'";
            ResultSet res = sq.executeQuery(sq_str_check);
            boolean CodeCheck = true;
            while (res.next()) {
                if (res.getString("code").equals(arrStr[2]) || res.getString("full_code").equals(arrStr[3])) {
                    CodeCheck = false;
                    s_res = "null" + "\r";
                    break;
                }
            }
            if (CodeCheck) {
                sq_str_check = "Select Text FROM systems";
                res = sq.executeQuery(sq_str_check);
                boolean SeriesCheck = false;

                while (res.next()) {
                    if (res.getString("Text").equals(arrStr[4])) {
                        SeriesCheck = true;
                        break;
                    }
                }
                if (!SeriesCheck) {
                    sq_str_check = "INSERT INTO systems(Text) VALUES ('" + arrStr[4] + "')";
                    sq.executeUpdate(sq_str_check);
                }
                sq_str_check = "SELECT id FROM belaz WHERE Series = '" + arrStr[5] + "' AND Model = '" + arrStr[6] + "'";
                res = sq.executeQuery(sq_str_check);
                res.next();
                String belaz_id = res.getString("id");
                sq_str_check = "SELECT id FROM systems WHERE Text = '" + arrStr[4] + "'";
                res = sq.executeQuery(sq_str_check);
                res.next();
                String sys_id = res.getString("id");
                sq_str_check = "SELECT id FROM belaz_systems WHERE systems_id = " + sys_id + " AND belaz_id = " + belaz_id;
                res = sq.executeQuery(sq_str_check);
                if (!res.next()) {
                    sq_str_check = "INSERT INTO belaz_systems(belaz_id, systems_id) VALUES (" + belaz_id + ", " + sys_id + ")";
                    sq.executeUpdate(sq_str_check);
                }
                sq_str_check = "UPDATE malfunction_code SET systems_id = " + sys_id + ", code = '" + arrStr[2] + "', full_code = '" + arrStr[3] + "' WHERE code = " + arrStr[1] + " AND systems_id = " + sys_id;
                sq.executeUpdate(sq_str_check);
                s_res = "1" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object GetSystems(String[] arrStr) {
        try {
            String sq_str_check = "SELECT systems.Text FROM systems INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[1] + "' AND belaz.Model = '" + arrStr[2] + "'";
            ResultSet res = sq.executeQuery(sq_str_check);
            StringBuffer buf = new StringBuffer();
            while (res.next()) {
                buf.append(res.getString("systems.Text") + ">>");
            }
            s_res = buf.toString() + "\r";
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object UpdateCodesPage(String[] arrStr) {
        try {
            ArrayList<String> strList = new ArrayList<String>();
            StringBuffer buf = new StringBuffer();
            String sq_str_check = "SELECT malfunction_code.id, malfunction_code.code, malfunction_code.full_code, systems.Text FROM malfunction_code INNER JOIN systems ON systems.id = malfunction_code.systems_id INNER JOIN belaz_malfunction_code ON malfunction_code.id = belaz_malfunction_code.malfunction_code_id INNER JOIN belaz ON belaz.id = belaz_malfunction_code.belaz_id WHERE belaz.Series = '" + arrStr[1] + "' AND belaz.Model = '" + arrStr[2] + "'";
            ResultSet res = sq.executeQuery(sq_str_check);
            while (res.next()) {
                strList.add(res.getString("malfunction_code.id"));
                strList.add(res.getString("malfunction_code.code"));
                strList.add(res.getString("malfunction_code.full_code"));
                strList.add(res.getString("systems.Text"));
            }
            res.close();
            for (int i = 0; i < strList.size(); i += 4) {
                sq_str_check = "SELECT malfunction_cause.text FROM malfunction_cause INNER JOIN malfunction_code_reasons ON malfunction_code_reasons.cause_id = malfunction_cause.id INNER JOIN malfunction_code ON malfunction_code.id = malfunction_code_reasons.code_id INNER JOIN belaz_malfunction_code ON belaz_malfunction_code.malfunction_code_id = malfunction_code.id INNER JOIN belaz ON belaz.id = belaz_malfunction_code.belaz_id INNER JOIN belaz_systems ON belaz_systems.belaz_id = belaz.id INNER JOIN systems ON belaz_systems.systems_id = systems.id WHERE malfunction_code.id = " + strList.get(i) + " AND belaz.Series = '" + arrStr[1] + "' AND belaz.Model = '" + arrStr[2] + "' AND systems.Text = '" + strList.get(i + 3) + "'";
                ResultSet res2 = sq.executeQuery(sq_str_check);
                buf.append(strList.get(i + 1) + ">>" + strList.get(i + 2) + ">>" + strList.get(i + 3) + ">>");
                while (res2.next()) {
                    buf.append(res2.getString("malfunction_cause.text") + "<<");
                }
                buf.append(">>");
                res2.close();
            }
            s_res = buf.toString() + "\r";
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object UpdateCausesPage(String[] arrStr) {
        try {
            if (causeAllocation.equals("0")) {
                String sq_str_check = "SELECT malfunction_cause.text FROM malfunction_cause INNER JOIN malfunction_code_reasons ON malfunction_code_reasons.cause_id = malfunction_cause.id INNER JOIN malfunction_code ON malfunction_code.id = malfunction_code_reasons.code_id INNER JOIN belaz_malfunction_code ON belaz_malfunction_code.malfunction_code_id = malfunction_code.id INNER JOIN belaz ON belaz.id = belaz_malfunction_code.belaz_id WHERE malfunction_code.full_code = '" + arrStr[3] + "' AND belaz.Series = '" + arrStr[1] + "' AND belaz.Model = '" + arrStr[2] + "'";
                ResultSet res = sq.executeQuery(sq_str_check);
                StringBuffer buf = new StringBuffer();
                while (res.next()) {
                    buf.append(res.getString("malfunction_cause.text") + ">>");
                }
                s_res = buf.toString() + "\r";
            } else {
                s_res = UpdateCausesPage2(arrStr);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object AddCause(String[] arrStr) {
        try {
            if (causeAllocation.equals("0")) {
                String sq_str_quary = "SELECT malfunction_cause.text FROM malfunction_cause INNER JOIN malfunction_code_reasons ON malfunction_code_reasons.cause_id = malfunction_cause.id INNER JOIN malfunction_code ON malfunction_code.id = malfunction_code_reasons.code_id INNER JOIN belaz_malfunction_code ON belaz_malfunction_code.malfunction_code_id = malfunction_code.id INNER JOIN belaz ON belaz.id = belaz_malfunction_code.belaz_id WHERE malfunction_cause.text = '" + arrStr[1] + "' AND malfunction_code.full_code = '" + arrStr[5] + "' AND belaz.Series = '" + arrStr[3] + "' AND belaz.Model = '" + arrStr[4] + "' AND malfunction_cause.systems_id = malfunction_code.systems_id";
                ResultSet res = sq.executeQuery(sq_str_quary);
                if (!res.next()) {
                    sq_str_quary = "SELECT malfunction_code.systems_id, malfunction_code.id FROM malfunction_code INNER JOIN belaz_malfunction_code ON belaz_malfunction_code.malfunction_code_id = malfunction_code.id INNER JOIN belaz ON belaz.id = belaz_malfunction_code.belaz_id WHERE malfunction_code.full_code = '" + arrStr[5] + "' AND belaz.Series = '" + arrStr[3] + "' AND belaz.Model = '" + arrStr[4] + "'";
                    res = sq.executeQuery(sq_str_quary);
                    res.next();
                    String system_id = res.getString("malfunction_code.systems_id");
                    String code_id = res.getString("malfunction_code.id");
                    sq_str_quary = "SELECT malfunction_cause.id FROM malfunction_cause INNER JOIN belaz_systems ON belaz_systems.systems_id = malfunction_cause.systems_id INNER JOIN belaz ON belaz_systems.belaz_id = belaz.id WHERE belaz.Series = '" + arrStr[3] + "' AND belaz.Model = '" + arrStr[4] + "' AND malfunction_cause.text = '" + arrStr[1] + "' AND malfunction_cause.systems_id = '" + system_id + "'";
                    res = sq.executeQuery(sq_str_quary);
                    if (!res.next()) {
                        sq_str_quary = "INSERT INTO malfunction_cause(systems_id, text, result) VALUES (" + system_id + ", '" + arrStr[1] + "', '" + arrStr[2] + "')";
                        sq.executeUpdate(sq_str_quary);
                    }
                    sq_str_quary = "SELECT malfunction_cause.id FROM malfunction_cause WHERE systems_id = " + system_id + " AND text = '" + arrStr[1] + "'";
                    res = sq.executeQuery(sq_str_quary);
                    res.next();
                    String cause_id = res.getString("id");
                    sq_str_quary = "INSERT INTO malfunction_code_reasons(code_id, cause_id) VALUES (" + code_id + ", " + cause_id + ")";
                    sq.executeUpdate(sq_str_quary);
                    s_res = "1" + "\r";
                } else {
                    s_res = "null" + "\r";
                }
            } else {
                s_res = AddCause2(arrStr);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object UpdateCause(String[] arrStr) {
        try {
            if (causeAllocation.equals("0")) {
                String sq_str_query = "SELECT malfunction_cause.text, malfunction_cause.result FROM malfunction_cause INNER JOIN malfunction_code_reasons ON malfunction_code_reasons.cause_id = malfunction_cause.id INNER JOIN malfunction_code ON malfunction_code.id = malfunction_code_reasons.code_id INNER JOIN belaz_malfunction_code ON belaz_malfunction_code.malfunction_code_id = malfunction_code.id INNER JOIN belaz ON belaz.id = belaz_malfunction_code.belaz_id WHERE malfunction_cause.text = '" + arrStr[2] + "' AND malfunction_code.full_code = '" + arrStr[6] + "' AND NOT malfunction_cause.text = '" + arrStr[1] + "' AND belaz.Series = '" + arrStr[4] + "' belaz.Model = '" + arrStr[5] + "'";
                ResultSet res = sq.executeQuery(sq_str_query);
                if (!res.next()) {
                    sq_str_query = "UPDATE malfunction_cause INNER JOIN malfunction_code_reasons ON malfunction_code_reasons.cause_id = malfunction_cause.id INNER JOIN malfunction_code ON malfunction_code.id = malfunction_code_reasons.code_id SET malfunction_cause.text = '" + arrStr[2] + "', malfunction_cause.result = '" + arrStr[3] + "' WHERE malfunction_cause.text = '" + arrStr[1] + "' AND malfunction_code.full_code = '" + arrStr[6] + "'";
                    sq.executeUpdate(sq_str_query);
                    s_res = "1" + "\r";
                } else {
                    s_res = "null" + "\r";
                }
            } else {
                s_res = UpdateCause2(arrStr);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object UpdateStepsPage(String[] arrStr) {
        try {
            StringBuffer buf = new StringBuffer();
            String sq_str_query = "SELECT resolve_steps.id, resolve_steps.Title, resolve_steps.Question FROM resolve_steps INNER JOIN malfunction_cause ON malfunction_cause.id = resolve_steps.cause_id INNER JOIN systems ON systems.id = malfunction_cause.systems_id INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE resolve_steps.step_number = " + arrStr[1] + " AND belaz.Series = '" + arrStr[2] + "' AND belaz.Model = '" + arrStr[3] + "' AND malfunction_cause.text = '" + arrStr[5] + "' AND malfunction_cause.systems_id = " + arrStr[6];
            ResultSet res = sq.executeQuery(sq_str_query);
            if (res.next()) {
                buf.append(res.getString("resolve_steps.Title") + ">>" + res.getString("resolve_steps.Question") + ">>");
                String id = res.getString("resolve_steps.id");
                sq_str_query = "SELECT text FROM actions WHERE steps_id = " + id + " ORDER BY action_number";
                res = sq.executeQuery(sq_str_query);
                while (res.next()) {
                    buf.append(res.getString("text") + "<<");
                }
                buf.append(">>");
                s_res = buf.toString() + "\r";
            } else {
                s_res = "" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object AddStep(String[] arrStr) {
        try {
            String sq_str_query = "SELECT resolve_steps.id, resolve_steps.Title, resolve_steps.Question FROM resolve_steps INNER JOIN malfunction_cause ON malfunction_cause.id = resolve_steps.cause_id INNER JOIN systems ON systems.id = malfunction_cause.systems_id INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[4] + "' AND belaz.Model = '" + arrStr[5] + "' AND malfunction_cause.text = '" + arrStr[7] + "' AND resolve_steps.Title = '" + arrStr[1] + "' AND resolve_steps.Question = '" + arrStr[2] + "' AND malfunction_cause.systems_id = " + arrStr[8];
            ResultSet res = sq.executeQuery(sq_str_query);
            if (!res.next()) {
                sq_str_query = "SELECT malfunction_cause.id FROM malfunction_cause INNER JOIN systems ON systems.id = malfunction_cause.systems_id INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[4] + "' AND belaz.Model = '" + arrStr[5] + "' AND malfunction_cause.text = '" + arrStr[7] + "' AND malfunction_cause.systems_id = " + arrStr[8];
                res = sq.executeQuery(sq_str_query);
                res.next();
                String id = res.getString("malfunction_cause.id");
                sq_str_query = "INSERT INTO resolve_steps(cause_id, Title, Question, step_number) VALUES (" + id + ", '" + arrStr[1] + "', '" + arrStr[2] + "', " + arrStr[3] + ")";
                sq.executeUpdate(sq_str_query);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object SaveActions(String[] arrStr) {
        try {
            String[] actions = arrStr[1].split("<<");
            int counter = 1;
            String sq_str_query = "DELETE actions FROM actions INNER JOIN resolve_steps ON actions.steps_id = resolve_steps.id INNER JOIN malfunction_cause ON malfunction_cause.id = resolve_steps.cause_id INNER JOIN systems ON systems.id = malfunction_cause.systems_id INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE resolve_steps.step_number = " + arrStr[2] + " AND belaz.Series = '" + arrStr[3] + "' AND belaz.Model = '" + arrStr[4] + "' AND malfunction_cause.text = '" + arrStr[6] + "' AND malfunction_cause.systems_id = '" + arrStr[7] + "'";
            sq.executeUpdate(sq_str_query);
            sq_str_query = "SELECT resolve_steps.id FROM resolve_steps INNER JOIN malfunction_cause ON malfunction_cause.id = resolve_steps.cause_id INNER JOIN systems ON systems.id = malfunction_cause.systems_id INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[3] + "' AND belaz.Model = '" + arrStr[4] + "' AND resolve_steps.step_number = " + arrStr[2] + " AND malfunction_cause.text = '" + arrStr[6] + "' AND malfunction_cause.systems_id = '" + arrStr[7] + "'";
            ResultSet res = sq.executeQuery(sq_str_query);
            res.next();
            String id = res.getString("resolve_steps.id");
            for (int i = 0; i < actions.length; i++) {
                sq_str_query = "INSERT INTO actions(steps_id, text, action_number) VALUES (" + id + ", '" + actions[i] + "', " + counter + ")";
                sq.executeUpdate(sq_str_query);
                counter++;
            }
            s_res = "1" + "\r";
        } catch (SQLException e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        return s_res;
    }

    public Object UpdateStep(String[] arrStr) {
        try {
            String sq_str_query = "SELECT resolve_steps.id, resolve_steps.Title, resolve_steps.Question FROM resolve_steps INNER JOIN malfunction_cause ON malfunction_cause.id = resolve_steps.cause_id INNER JOIN systems ON systems.id = malfunction_cause.systems_id INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[5] + "' AND belaz.Model = '" + arrStr[6] + "' AND malfunction_cause.text = '" + arrStr[8] + "' AND resolve_steps.Title = '" + arrStr[2] + "' AND resolve_steps.Question = '" + arrStr[3] + "' AND NOT resolve_steps.Title = '" + arrStr[1] + "' AND malfunction_cause.systems_id = " + arrStr[9];
            ResultSet res = sq.executeQuery(sq_str_query);
            if (!res.next()) {
                sq_str_query = "UPDATE resolve_steps INNER JOIN malfunction_cause ON malfunction_cause.id = resolve_steps.cause_id INNER JOIN systems ON systems.id = malfunction_cause.systems_id INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id SET resolve_steps.Title = '" + arrStr[2] + "', resolve_steps.Question = '" + arrStr[3] + "' WHERE belaz.Series = '" + arrStr[5] + "' AND belaz.Model = '" + arrStr[6] + "' AND malfunction_cause.text = '" + arrStr[8] + "' AND malfunction_cause.systems_id = " + arrStr[9];
                sq.executeUpdate(sq_str_query);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object UpdateSystemsPage(String[] arrStr) {
        try {
            String sq_str_query = "SELECT systems.Text FROM systems INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[1] + "' AND belaz.Model = '" + arrStr[2] + "'";
            ResultSet res = sq.executeQuery(sq_str_query);
            StringBuffer buf = new StringBuffer();
            while (res.next()) {
                buf.append(res.getString("systems.Text") + ">>");
            }
            s_res = buf.toString() + "\r";
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object AddSystem(String[] arrStr) {
        try {
            String sq_str_query = "SELECT systems.id FROM systems INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[2] + "' AND belaz.model = '" + arrStr[3] + "' AND systems.Text = '" + arrStr[1] + "'";
            ResultSet res = sq.executeQuery(sq_str_query);
            if (!res.next()) {
                //sq_str_query = "SELECT id FROM systems WHERE Text = '" + arrStr[1] + "'";
                //res = sq.executeQuery(sq_str_query);
                // if(!res.next()) {
                sq_str_query = "INSERT INTO systems(Text) VALUES ('" + arrStr[1] + "')";
                sq.executeUpdate(sq_str_query);
                //}
                sq_str_query = "SELECT id FROM systems WHERE Text = '" + arrStr[1] + "'";
                res = sq.executeQuery(sq_str_query);
                res.next();
                String systems_id = res.getString("id");
                sq_str_query = "SELECT id FROM belaz WHERE Series = '" + arrStr[2] + "' AND Model = '" + arrStr[3] + "'";
                res = sq.executeQuery(sq_str_query);
                res.next();
                String belaz_id = res.getString("id");
                sq_str_query = "INSERT INTO belaz_systems(systems_id, belaz_id) VALUES (" + systems_id + ", " + belaz_id + ")";
                sq.executeUpdate(sq_str_query);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object UpdateSystem(String[] arrStr) {
        try {
            String sq_str_query = "SELECT systems.id FROM systems INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[3] + "' AND belaz.model = '" + arrStr[4] + "' AND systems.Text = '" + arrStr[2] + "' AND NOT sistems.Text = '" + arrStr[1] + "'";
            ResultSet res = sq.executeQuery(sq_str_query);
            if (!res.next()) {
                sq_str_query = "UPDATE systems SET Text = '" + arrStr[2] + "' WHERE Text = '" + arrStr[1] + "'";
                sq.executeUpdate(sq_str_query);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object DeleteSystem(String[] arrStr) {
        try {
            String sq_str_quary = "DELETE FROM systems WHERE Text = '" + arrStr[1] + "'";
            sq.executeUpdate(sq_str_quary);
            s_res = "1" + "\r";
        } catch (SQLException e) {
            s_res = "null" + "\r";
            System.out.println(e);
        }
        return s_res;
    }

    public Object UpdateManifestationsPage(String[] arrStr) {
        try {
            StringBuffer buf = new StringBuffer();
            String sq_str_check = "SELECT malfunction_manifestation.id, malfunction_manifestation.Test, systems.Text FROM malfunction_manifestation INNER JOIN systems ON systems.id = malfunction_manifestation.systems_id INNER JOIN belaz_malfunction_manifestation ON malfunction_manifestation.id = belaz_malfunction_manifestation.malfunction_manifestation_id INNER JOIN belaz ON belaz.id = belaz_malfunction_manifestation.belaz_id WHERE belaz.Series = '" + arrStr[1] + "' AND belaz.Model = '" + arrStr[2] + "' AND systems.text = '" + arrStr[3] + "'";
            ResultSet res = sq.executeQuery(sq_str_check);
            while (res.next()) {
                buf.append(res.getString("malfunction_manifestation.Test") + ">>");
            }
            s_res = buf.toString() + "\r";
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object AddManifestation(String[] arrStr) {
        try {
            String sq_str_query = "SELECT malfunction_manifestation.id, malfunction_manifestation.Test, systems.Text FROM malfunction_manifestation INNER JOIN systems ON systems.id = malfunction_manifestation.systems_id INNER JOIN belaz_malfunction_manifestation ON malfunction_manifestation.id = belaz_malfunction_manifestation.malfunction_manifestation_id INNER JOIN belaz ON belaz.id = belaz_malfunction_manifestation.belaz_id WHERE belaz.Series = '" + arrStr[2] + "' AND belaz.Model = '" + arrStr[3] + "' AND systems.Text = '" + arrStr[4] + "' AND malfunction_manifestation.Test = '" + arrStr[1] + "'";
            ResultSet res = sq.executeQuery(sq_str_query);
            if (!res.next()) {
                sq_str_query = "SELECT id FROM belaz WHERE Series = '" + arrStr[2] + "' AND Model = '" + arrStr[3] + "'";
                res = sq.executeQuery(sq_str_query);
                res.next();
                String belaz_id = res.getString("id");
                sq_str_query = "SELECT systems.id FROM systems INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[2] + "' AND belaz.Model = '" + arrStr[3] + "' AND systems.Text = '" + arrStr[4] + "'";
                res = sq.executeQuery(sq_str_query);
                res.next();
                sq_str_query = "INSERT INTO malfunction_manifestation(systems_id, Test) VALUES (" + res.getString("systems.id") + ", '" + arrStr[1] + "')";
                sq.executeUpdate(sq_str_query);
                sq_str_query = "SELECT malfunction_manifestation.id FROM malfunction_manifestation INNER JOIN systems ON systems.id = malfunction_manifestation.systems_id INNER JOIN belaz_systems ON systems.id = belaz_systems.systems_id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[2] + "' AND belaz.Model = '" + arrStr[3] + "' AND systems.Text = '" + arrStr[4] + "' AND malfunction_manifestation.Test = '" + arrStr[1] + "'";
                res = sq.executeQuery(sq_str_query);
                res.next();
                sq_str_query = "INSERT INTO belaz_malfunction_manifestation(belaz_id, malfunction_manifestation_id) VALUES (" + belaz_id + ", " + res.getString("malfunction_manifestation.id") + ")";
                sq.executeUpdate(sq_str_query);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object UpdateManifestation(String[] arrStr) {
        try {
            String sq_str_query = "SELECT malfunction_manifestation.id, malfunction_manifestation.Test, systems.Text FROM malfunction_manifestation INNER JOIN systems ON systems.id = malfunction_manifestation.systems_id INNER JOIN belaz_malfunction_manifestation ON malfunction_manifestation.id = belaz_malfunction_manifestation.malfunction_manifestation_id INNER JOIN belaz ON belaz.id = belaz_malfunction_manifestation.belaz_id WHERE belaz.Series = '" + arrStr[3] + "' AND belaz.Model = '" + arrStr[4] + "' AND systems.Text = '" + arrStr[5] + "' AND malfunction_manifestation.Test = '" + arrStr[2] + "' AND NOT malfunction_manifestation.Test = '" + arrStr[1] + "'";
            ResultSet res = sq.executeQuery(sq_str_query);
            if (!res.next()) {
                sq_str_query = "UPDATE malfunction_manifestation INNER JOIN systems ON systems.id = malfunction_manifestation.systems_id INNER JOIN belaz_malfunction_manifestation ON malfunction_manifestation.id = belaz_malfunction_manifestation.malfunction_manifestation_id INNER JOIN belaz ON belaz.id = belaz_malfunction_manifestation.belaz_id SET malfunction_manifestation.Test = '" + arrStr[2] + "' WHERE belaz.Series = '" + arrStr[3] + "' AND belaz.Model = '" + arrStr[4] + "' AND systems.Text = '" + arrStr[5] + "' AND malfunction_manifestation.Test = '" + arrStr[1] + "'";
                sq.executeUpdate(sq_str_query);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object DeleteManifestation(String[] arrStr) {
        try {
            String sq_str_query = "DELETE malfunction_manifestation FROM malfunction_manifestation INNER JOIN systems ON systems.id = malfunction_manifestation.systems_id INNER JOIN belaz_malfunction_manifestation ON malfunction_manifestation.id = belaz_malfunction_manifestation.malfunction_manifestation_id INNER JOIN belaz ON belaz.id = belaz_malfunction_manifestation.belaz_id WHERE belaz.Series = '" + arrStr[2] + "' AND belaz.Model = '" + arrStr[3] + "' AND systems.Text = '" + arrStr[4] + "' AND malfunction_manifestation.Test = '" + arrStr[1] + "'";
            sq.executeUpdate(sq_str_query);
            s_res = "1" + "\r";
        } catch (SQLException e) {
            s_res = "null" + "\r";
            System.out.println(e);
        }
        return s_res;
    }

    public Object GetSystemOnce(String[] arrStr) {
        try {
            if (causeAllocation.equals("0")) {
                String sq_str_check = "SELECT systems.id FROM systems INNER JOIN malfunction_code ON malfunction_code.systems_id = systems.id INNER JOIN belaz_malfunction_code ON belaz_malfunction_code.malfunction_code_id = malfunction_code.id INNER JOIN belaz ON belaz.id = belaz_malfunction_code.belaz_id WHERE belaz.Series = '" + arrStr[1] + "' AND belaz.Model = '" + arrStr[2] + "' AND malfunction_code.full_code = '" + arrStr[3] + "'";
                ResultSet res = sq.executeQuery(sq_str_check);
                res.next();
                s_res = res.getString("systems.id") + "\r";
            } else {
                s_res = GetSystemOnce2(arrStr);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public void SetCauseAllocation(String[] arrStr) {
        causeAllocation = arrStr[1];
    }

    //===================================================================================
    //===================================================================================
    public String UpdateCausesPage2(String[] arrStr) {
        System.out.println("!!!!!!!!!!!!!!!!!!!");
        String s_res = "";
        try {
            String sq_str_check = "SELECT malfunction_cause.text FROM malfunction_cause INNER JOIN malfunction_manifestation_reasons ON malfunction_manifestation_reasons.cause_id = malfunction_cause.id INNER JOIN malfunction_manifestation ON malfunction_manifestation.id = malfunction_manifestation_reasons.manifestation_id INNER JOIN belaz_malfunction_manifestation ON belaz_malfunction_manifestation.malfunction_manifestation_id = malfunction_manifestation.id INNER JOIN belaz ON belaz.id = belaz_malfunction_manifestation.belaz_id WHERE malfunction_manifestation.Test = '" + arrStr[3] + "' AND belaz.Series = '" + arrStr[1] + "' AND belaz.Model = '" + arrStr[2] + "' AND malfunction_manifestation.systems_id = malfunction_cause.systems_id";
            ResultSet res = sq.executeQuery(sq_str_check);
            StringBuffer buf = new StringBuffer();
            while (res.next()) {
                buf.append(res.getString("malfunction_cause.text") + ">>");
            }
            s_res = buf.toString() + "\r";
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public String AddCause2(String[] arrStr) {
        String s_res = "";
        try {
            String sq_str_quary = "SELECT malfunction_cause.text FROM malfunction_cause INNER JOIN malfunction_manifestation_reasons ON malfunction_manifestation_reasons.cause_id = malfunction_cause.id INNER JOIN malfunction_manifestation ON malfunction_manifestation.id = malfunction_manifestation_reasons.manifestation_id INNER JOIN belaz_malfunction_manifestation ON belaz_malfunction_manifestation.malfunction_manifestation_id = malfunction_manifestation.id INNER JOIN belaz ON belaz.id = belaz_malfunction_manifestation.belaz_id WHERE malfunction_cause.text = '" + arrStr[1] + "' AND malfunction_manifestation.Test = '" + arrStr[5] + "' AND belaz.Series = '" + arrStr[3] + "' AND belaz.Model = '" + arrStr[4] + "' AND malfunction_cause.systems_id = malfunction_manifestation.systems_id";
            ResultSet res = sq.executeQuery(sq_str_quary);
            if (!res.next()) {
                sq_str_quary = "SELECT malfunction_manifestation.systems_id, malfunction_manifestation.id FROM malfunction_manifestation INNER JOIN belaz_malfunction_manifestation ON belaz_malfunction_manifestation.malfunction_manifestation_id = malfunction_manifestation.id INNER JOIN belaz ON belaz.id = belaz_malfunction_manifestation.belaz_id WHERE malfunction_manifestation.Test = '" + arrStr[5] + "' AND belaz.Series = '" + arrStr[3] + "' AND belaz.Model = '" + arrStr[4] + "'";
                res = sq.executeQuery(sq_str_quary);
                res.next();
                String system_id = res.getString("malfunction_manifestation.systems_id");
                String code_id = res.getString("malfunction_manifestation.id");
                sq_str_quary = "SELECT malfunction_cause.id FROM malfunction_cause INNER JOIN belaz_systems ON belaz_systems.systems_id = malfunction_cause.systems_id INNER JOIN belaz ON belaz_systems.belaz_id = belaz.id WHERE belaz.Series = '" + arrStr[3] + "' AND belaz.Model = '" + arrStr[4] + "' AND malfunction_cause.text = '" + arrStr[1] + "' AND malfunction_cause.systems_id = '" + system_id + "'";
                res = sq.executeQuery(sq_str_quary);
                if (!res.next()) {
                    sq_str_quary = "INSERT INTO malfunction_cause(systems_id, text, result) VALUES (" + system_id + ", '" + arrStr[1] + "', '" + arrStr[2] + "')";
                    sq.executeUpdate(sq_str_quary);
                }
                sq_str_quary = "SELECT malfunction_cause.id FROM malfunction_cause WHERE systems_id = " + system_id + " AND text = '" + arrStr[1] + "'";
                res = sq.executeQuery(sq_str_quary);
                res.next();
                String cause_id = res.getString("id");
                sq_str_quary = "INSERT INTO malfunction_manifestation_reasons(manifestation_id, cause_id) VALUES (" + code_id + ", " + cause_id + ")";
                sq.executeUpdate(sq_str_quary);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public String UpdateCause2(String[] arrStr) {
        String s_res = "";
        try {
            String sq_str_query = "SELECT malfunction_cause.text, malfunction_cause.result FROM malfunction_cause INNER JOIN malfunction_manifestation_reasons ON malfunction_manifestation_reasons.cause_id = malfunction_cause.id INNER JOIN malfunction_manifestation ON malfunction_manifestation.id = malfunction_manifestation_reasons.manifestation_id INNER JOIN belaz_malfunction_manifestation ON belaz_malfunction_manifestation.malfunction_manifestation_id = malfunction_manifestation.id INNER JOIN belaz ON belaz.id = belaz_malfunction_manifestation.belaz_id WHERE malfunction_cause.text = '" + arrStr[2] + "' AND malfunction_manifestation.Test = '" + arrStr[6] + "' AND NOT malfunction_cause.text = '" + arrStr[1] + "' AND belaz.Series = '" + arrStr[4] + "' belaz.Model = '" + arrStr[5] + "'";
            ResultSet res = sq.executeQuery(sq_str_query);
            if (!res.next()) {
                sq_str_query = "UPDATE malfunction_cause INNER JOIN malfunction_manifestation_reasons ON malfunction_manifestation_reasons.cause_id = malfunction_cause.id INNER JOIN malfunction_manifestation ON malfunction_manifestation.id = malfunction_manifestation_reasons.manifestation_id SET malfunction_cause.text = '" + arrStr[2] + "', malfunction_cause.result = '" + arrStr[3] + "' WHERE malfunction_cause.text = '" + arrStr[1] + "' AND malfunction_manifestation.Test = '" + arrStr[6] + "'";
                sq.executeUpdate(sq_str_query);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public String GetSystemOnce2(String[] arrStr) {
        String s_res = "";
        try {
            String sq_str_check = "SELECT systems.id FROM systems INNER JOIN malfunction_manifestation ON malfunction_manifestation.systems_id = systems.id INNER JOIN belaz_malfunction_manifestation ON belaz_malfunction_manifestation.malfunction_manifestation_id = malfunction_manifestation.id INNER JOIN belaz ON belaz.id = belaz_malfunction_manifestation.belaz_id WHERE belaz.Series = '" + arrStr[1] + "' AND belaz.Model = '" + arrStr[2] + "' AND malfunction_manifestation.Test = '" + arrStr[3] + "'";
            ResultSet res = sq.executeQuery(sq_str_check);
            res.next();
            s_res = res.getString("systems.id") + "\r";
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    //=======================================================================
    //=======================================================================
    public Object GetCauseResult(String[] arrStr) {
        try {
            String sq_str_query = "SELECT resolve_steps.id, malfunction_cause.result FROM resolve_steps INNER JOIN malfunction_cause ON malfunction_cause.id = resolve_steps.cause_id INNER JOIN systems ON systems.id = malfunction_cause.systems_id INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[1] + "' AND belaz.Model = '" + arrStr[2] + "' AND malfunction_cause.text = '" + arrStr[4] + "' AND malfunction_cause.systems_id = " + arrStr[5];
            ResultSet res = sq.executeQuery(sq_str_query);
            res.next();
            s_res = res.getString("malfunction_cause.result") + "\r";
        } catch (SQLException e) {
            System.out.println(e);
        }
        return s_res;
    }

    public Object DeleteCode(String[] arrStr) {
        try {
            String sq_str_query = "DELETE malfunction_code FROM malfunction_code inner join belaz_malfunction_code ON belaz_malfunction_code.malfunction_code_id = malfunction_code.id INNER JOIN belaz ON belaz.id = belaz_malfunction_code.belaz_id WHERE belaz.Series = '" + arrStr[2] + "' AND belaz.Model = '" + arrStr[3] + "' AND malfunction_code.full_code = '" + arrStr[1] + "'";
            sq.executeUpdate(sq_str_query);
            s_res = "1" + "\r";
        } catch (SQLException e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        return s_res;
    }

    public Object DeleteCause(String[] arrStr) {
        if (causeAllocation.equals("0")) {
            try {
                String sq_str_query = "DELETE malfunction_cause FROM malfunction_cause INNER JOIN malfunction_code_reasons ON malfunction_code_reasons.cause_id = malfunction_cause.id INNER JOIN malfunction_code ON malfunction_code.id = malfunction_code_reasons.code_id INNER JOIN belaz_malfunction_code ON belaz_malfunction_code.malfunction_code_id = malfunction_code.id INNER JOIN belaz ON belaz.id = belaz_malfunction_code.belaz_id WHERE malfunction_cause.text = '" + arrStr[1] + "' AND malfunction_code.full_code = '" + arrStr[4] + "' AND belaz.Series = '" + arrStr[2] + "' AND belaz.Model = '" + arrStr[3] + "' AND malfunction_cause.systems_id = malfunction_code.systems_id";
                sq.executeUpdate(sq_str_query);
                s_res = "1" + "\r";
            } catch (SQLException e) {
                System.out.println(e);
                s_res = "null" + "\r";
            }
        } else {
            s_res = DeleteCause2(arrStr);
        }
        return s_res;
    }

    public String DeleteCause2(String[] arrStr) {
        String s_res;
        try {
            String sq_str_query = "DELETE malfunction_cause FROM malfunction_cause INNER JOIN malfunction_manifestation_reasons ON malfunction_manifestation_reasons.cause_id = malfunction_cause.id INNER JOIN malfunction_manifestation ON malfunction_manifestation.id = malfunction_manifestation_reasons.manifestation_id INNER JOIN belaz_malfunction_manifestation ON belaz_malfunction_manifestation.malfunction_manifestation_id = malfunction_manifestation.id INNER JOIN belaz ON belaz.id = belaz_malfunction_manifestation.belaz_id WHERE malfunction_cause.text = '" + arrStr[1] + "' AND malfunction_manifestation.Test = '" + arrStr[4] + "' AND belaz.Series = '" + arrStr[2] + "' AND belaz.Model = '" + arrStr[3] + "' AND malfunction_cause.systems_id = malfunction_manifestation.systems_id";
            sq.executeUpdate(sq_str_query);
            s_res = "1" + "\r";
        } catch (SQLException e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        return s_res;
    }

    public Object DeleteStep(String[] arrStr) {
        try {
            String sq_str_query = "DELETE resolve_steps FROM resolve_steps INNER JOIN malfunction_cause ON malfunction_cause.id = resolve_steps.cause_id INNER JOIN systems ON systems.id = malfunction_cause.systems_id INNER JOIN belaz_systems ON belaz_systems.systems_id = systems.id INNER JOIN belaz ON belaz.id = belaz_systems.belaz_id WHERE belaz.Series = '" + arrStr[4] + "' AND belaz.Model = '" + arrStr[5] + "' AND malfunction_cause.text = '" + arrStr[7] + "' AND resolve_steps.Title = '" + arrStr[1] + "' AND resolve_steps.Question = '" + arrStr[2] + "' AND malfunction_cause.systems_id = " + arrStr[8];
            sq.executeUpdate(sq_str_query);
            s_res = "1" + "\r";
        } catch (SQLException e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        return s_res;
    }

    public Object DeleteSeries(String[] arrStr) {
        try {
            String sq_str_query = "DELETE FROM belaz WHERE Series = '" + arrStr[1] + "'";
            sq.executeUpdate(sq_str_query);
            s_res = "1" + "\r";
        } catch (SQLException e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        return s_res;
    }

    @Override
    public Object UpdateSeries(String[] arrStr) {
        try {
            String sq_str_query = "SELECT * FROM belaz WHERE Series = '" + arrStr[2] + "' AND NOT Series = '" + arrStr[1] + "'";
            ResultSet res = sq.executeQuery(sq_str_query);
            if (!res.next()) {
                sq_str_query = "UPDATE belaz SET Series = '" + arrStr[2] + "' WHERE Series = '" + arrStr[1] + "'";
                sq.executeUpdate(sq_str_query);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        return s_res;
    }

    @Override
    public Object DeleteModel(String[] arrStr) {
        try {
            String sq_str_query = "DELETE FROM belaz WHERE Series = '" + arrStr[2] + "' AND Model = '" + arrStr[1] + "'";
            sq.executeUpdate(sq_str_query);
            s_res = "1" + "\r";
        } catch (SQLException e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        return s_res;
    }

    @Override
    public Object UpdateModel(String[] arrStr) {
        try {
            String sq_str_query = "SELECT * FROM belaz WHERE Series = '" + arrStr[4] + "' AND NOT Model = '" + arrStr[1] + "' AND Model = '" + arrStr[2] + "'";
            ResultSet res = sq.executeQuery(sq_str_query);
            if (!res.next()) {
                sq_str_query = "UPDATE belaz SET Model = '" + arrStr[2] + "', imageM = '" + arrStr[3] + "' WHERE Series = '" + arrStr[4] + "' AND Model = '" + arrStr[1] + "'";
                sq.executeUpdate(sq_str_query);
                s_res = "1" + "\r";
            } else {
                s_res = "null" + "\r";
            }
        } catch (SQLException e) {
            System.out.println(e);
            s_res = "null" + "\r";
        }
        return s_res;
    }

    public String CreateImageName() throws IOException {
        File dir = new File("D:\\FCP\\SEM7\\CURS\\Project\\DataLib\\src\\main\\resources\\images");
        Random rand = new Random();
        String fileName = String.valueOf(rand.nextInt(100000000)) + ".png";
        for (final File f : dir.listFiles()) {
            // BufferedImage img = null;

            try {
                System.out.println(f.getName());
                if (fileName.equals(f.getName())) {
                    fileName = CreateImageName();
                }
//                    img = ImageIO.read(f);
//
//                    System.out.println("image: " + f.getName());
//                    System.out.println(" width : " + img.getWidth());
//                    System.out.println(" height: " + img.getHeight());
//                    System.out.println(" size  : " + f.length());
            }
            catch (final IOException e) {

            }
        }
        return fileName;
    }
}