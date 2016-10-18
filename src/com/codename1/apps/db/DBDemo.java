package com.codename1.apps.db;

import com.codename1.components.MultiButton;
import com.codename1.components.ScaleImageButton;
import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import com.codename1.db.Cursor;
import com.codename1.db.Database;
import com.codename1.db.Row;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.io.Util;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.TextArea;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.DefaultTableModel;
import com.codename1.ui.table.Table;
import com.codename1.ui.util.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * This demo is constructed as an SQL tutorial to allow us to  ship it in the appstores
 */
public class DBDemo {
    private Form currentForm;
    private Resources theme;

    public void init(Object context) {
        theme = UIManager.initFirstTheme("/theme");
        Toolbar.setGlobalToolbar(true);
        
        // install a default DB
        String path = Display.getInstance().getDatabasePath("MyDB.db");
        if(path != null && !FileSystemStorage.getInstance().exists(path)) {
            copyDb(path);
        }
    }

    private void copyDb(String path) {
        try(InputStream i = Display.getInstance().getResourceAsStream(getClass(), "/MyDB.db");
                OutputStream o = FileSystemStorage.getInstance().openOutputStream(path)) {
            Util.copy(i, o);
        } catch(IOException err) {
            Log.e(err);
        }
    }
    
    public void start() {
        if(currentForm != null) {
            currentForm.show();
            return;
        }
        
        Form hi = new Form("SQL Playground", new BorderLayout());
        hi.getToolbar().addMaterialCommandToRightBar("", 
                FontImage.MATERIAL_QUERY_BUILDER, 
                e -> showQueryDialog(hi, ""));
        
        ScaleImageButton btn = new ScaleImageButton(theme.getImage("icon.png"));
        btn.setUIID("Container");
        btn.addActionListener(e -> Display.getInstance().execute("https://www.codenameone.com/"));
        hi.getToolbar().addComponentToSideMenu(btn);
        hi.getToolbar().addMaterialCommandToSideMenu("Reset DB", 
                FontImage.MATERIAL_CLEAR, 
                e -> copyDb(Display.getInstance().getDatabasePath("MyDB.db")));
        
        hi.getToolbar().addMaterialCommandToSideMenu("List Tables", 
                FontImage.MATERIAL_LIST, 
                e -> listTables(hi));

        hi.getToolbar().addMaterialCommandToSideMenu("Tutorial", 
                FontImage.MATERIAL_PLAYLIST_PLAY, 
                e -> tutorial(hi));

        
        hi.add(BorderLayout.CENTER, new SpanLabel("SQL Playground provides a simple experiment driven tutorial"
                + " for SQL syntax while focusing on sqlite. It isn't meant as a replacement to \"proper\" books or training"
                + " materials but rather as a tool to quicklt experiment on your device of choice.\n\n"
                + "To start the tutorial mode just click the option in the side menu."));
        
        hi.show();
    }
    
    /**
     * The tutorial is based on this blog post: http://www.thegeekstuff.com/2012/09/sqlite-command-examples/
     */
    public void tutorial(Form hi) {
        if(!Dialog.show("Create","Let's start by creaing an employees table, notice that it is recommeded that you reset the database before starting the tutorial....", 
                "Continue", "Cancel")) {
            return;
        }
        if(!showQueryDialog(hi, "create table employee(empid integer,name varchar(20),title varchar(10))")) {
            return;
        }
        if(!Dialog.show("Create","Next, let's try to create an employee department table", "Continue", "Cancel")) {
            return;
        }
        if(!showQueryDialog(hi, "create table department(deptid integer,name varchar(20),location varchar(10))")) {
            return;
        }
        if(!Dialog.show("Insert","Lets insert 3 employees into the table...", "Continue", "Cancel")) {
            return;
        }
        if(!showQueryDialog(hi, "insert into employee values(101,'John Smith','CEO')")) {
            return;
        }
        if(!showQueryDialog(hi, "insert into employee values(102,'Raj Reddy','Sysadmin')")) {
            return;
        }
        if(!showQueryDialog(hi, "insert into employee values(103,'Jason Bourne','Developer')")) {
            return;
        }
        if(!Dialog.show("Select","Lets check out what we did!", "Continue", "Cancel")) {
            return;
        }
        if(!showQueryDialog(hi, "select * from employee")) {
            return;
        }
        int tint = hi.getTintColor();
        hi.setTintColor(0);
        Dialog.setDefaultDialogPosition(BorderLayout.SOUTH);
        if(!Dialog.show("Insert","Lets insert 2 deparments...", "Continue", "Cancel")) {
            Dialog.setDefaultDialogPosition(BorderLayout.CENTER);
            hi.setTintColor(tint);
            return;
        }
        Dialog.setDefaultDialogPosition(BorderLayout.CENTER);
        hi.setTintColor(tint);
        if(!showQueryDialog(hi, "insert into department values(1,'Sales','Los Angeles')")) {
            return;
        }
        if(!showQueryDialog(hi, "insert into department values(2,'Technology','San Jose')")) {
            return;
        }
        if(!Dialog.show("Alter","Now we'll rename department to dept with all the data within...", "Continue", "Cancel")) {
            return;
        }
        if(!showQueryDialog(hi, "alter table department rename to dept")) {
            return;
        }
        if(!Dialog.show("Alter","We'll add a column to the employee table to point at the department...", "Continue", "Cancel")) {
            return;
        }
        if(!showQueryDialog(hi, "alter table employee add column deptid integer")) {
            return;
        }
        if(!Dialog.show("Update","Now lets update our 3 employees to have a department...", "Continue", "Cancel")) {
            return;
        }
        if(!showQueryDialog(hi, "update employee set deptid=2 where empid=101")) {
            return;
        }
        if(!showQueryDialog(hi, "update employee set deptid=2 where empid=102")) {
            return;
        }
        if(!showQueryDialog(hi, "update employee set deptid=2 where empid=103")) {
            return;
        }
        if(!Dialog.show("Index","We'll create a unique index for employee id...", "Continue", "Cancel")) {
            return;
        }
        if(!showQueryDialog(hi, "create unique index empidx on employee(empid)")) {
            return;
        }
        if(!Dialog.show("Index","Now that we have an index an invalid insert (of something we already have) will fail!", "Continue", "Cancel")) {
            return;
        }
        showQueryDialog(hi, "insert into employee values (101,'James Bond','Secret Agent',1)");
        
        Dialog.show("Thanks", "That's it for the current tutorial, thanks for going thru it!\nCheck out the results in the table list", "OK", null);
    }
    
    
    void listTables(Form hi) {
        Database db = null;
        Cursor cur = null;
        try {
            db = Display.getInstance().openOrCreate("MyDB.db");
            cur = db.executeQuery("select * from sqlite_master");
            hi.removeAll();
            Container b = new Container(BoxLayout.y());
            b.setScrollableY(true);
            hi.add(BorderLayout.CENTER, b);
            while(cur.next()) {
                int idx = cur.getColumnIndex("name");
                String name = cur.getRow().getString(idx);
                
                MultiButton tbl = new MultiButton(name);
                FontImage.setMaterialIcon(tbl, FontImage.MATERIAL_STORAGE);
                Button delete = new Button();
                FontImage.setMaterialIcon(delete, FontImage.MATERIAL_DELETE);
                b.add(BorderLayout.center(tbl).
                        add(BorderLayout.EAST, delete));
                tbl.addActionListener(e -> showQueryDialog(hi, "select * from " + name));
                delete.addActionListener(e -> showQueryDialog(hi, "drop table " + name));
            }
            hi.revalidate();
        } catch(IOException err) {
            Log.e(err);
            hi.removeAll();
            hi.revalidate();
            ToastBar.showErrorMessage("Error: " + err);
        } finally {
            Util.cleanup(db);
            Util.cleanup(cur);
        }
    }

    boolean showQueryDialog(Form hi, String queryString) {
        TextArea query = new TextArea(queryString, 3, 80);
        Command ok = new Command("Execute");
        Command cancel = new Command("Cancel");
        boolean result = true;
        if(Dialog.show("Query", query, cancel, ok) == ok) {
            Database db = null;
            Cursor cur = null;
            try {
                db = Display.getInstance().openOrCreate("MyDB.db");
                if(query.getText().startsWith("select")) {
                    cur = db.executeQuery(query.getText());
                    int columns = cur.getColumnCount();
                    hi.removeAll();
                    if(columns > 0) {
                        boolean next = cur.next();
                        if(next) {
                            ArrayList<String[]> data = new ArrayList<>();
                            String[] columnNames = new String[columns];
                            for(int iter = 0 ; iter < columns ; iter++) {
                                columnNames[iter] = cur.getColumnName(iter);
                            }
                            while(next) {
                                Row currentRow = cur.getRow();
                                String[] currentRowArray = new String[columns];
                                for(int iter = 0 ; iter < columns ; iter++) {
                                    currentRowArray[iter] = currentRow.getString(iter);
                                }
                                data.add(currentRowArray);
                                next = cur.next();
                            }
                            Object[][] arr = new Object[data.size()][];
                            data.toArray(arr);
                            Table tbl = new Table(new DefaultTableModel(columnNames, arr)) {
                                @Override
                                protected Component createCell(Object value, int row, int column, boolean editable) {
                                    Component c = super.createCell(value, row, column, editable);
                                    if(row > -1) {
                                        if(row % 2 == 0) {
                                            c.setUIID("TableCellEven");
                                        } else {
                                            c.setUIID("TableCell");
                                        }
                                    } 
                                    return c;
                                }
                            };
                            tbl.setDrawBorder(false);
                            hi.add(BorderLayout.CENTER, tbl);
                        } else {
                            ToastBar.showMessage("Query returned no results", FontImage.MATERIAL_INFO);
                        }
                    } else {
                        ToastBar.showMessage("Query returned no results", FontImage.MATERIAL_INFO);
                    }
                } else {
                    db.execute(query.getText());
                    ToastBar.showMessage("Query completed successfully", FontImage.MATERIAL_INFO);
                }
                hi.revalidate();
            } catch(IOException err) {
                Log.e(err);
                hi.removeAll();
                hi.revalidate();
                ToastBar.showErrorMessage("Error: " + err);
                result = false;
            } finally {
                Util.cleanup(db);
                Util.cleanup(cur);
            }
        } else {
            return false;
        }
        return result;
    }
    
    public void stop() {
        currentForm = Display.getInstance().getCurrent();
    }

    public void destroy() {
    }
}
