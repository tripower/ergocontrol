package forms;

import javax.swing.*;

import data.ErgoUserData;

import java.util.Properties;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;


/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 21.02.2005
 * Time: 13:08:07
 * To change this template use File | Settings | File Templates.
 */
public class ErgoControlUser extends JDialog implements ActionListener, WindowListener {
	static final long serialVersionUID = 0;
	
    private static Properties myProperties = null;

    private static String DIALOGSTRING = "ErgoControl User";
    private static String LABEL_USERNAME = "Username";
    private static String BUTTON_CONFIRM = "Ok";
    private static String BUTTON_ABORT = "Cancel";

    private static String ERROR_USER_EMPTY = "Username is empty!\nPlease enter at least one character.";

    private static int WINDOW_WIDTH = 250;
    private static int WINDOW_HEIGHT = 80;

    private JTextField myUserName = new JTextField();
    private JButton myConfirm = new JButton(BUTTON_CONFIRM);
    private JButton myAbort = new JButton(BUTTON_ABORT);

    private ErgoUserData data = null;

    public ErgoControlUser(JFrame owner, Properties properties)
    {
        super(owner, DIALOGSTRING, true);

        myProperties = properties;

        setResizable(false);
        setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        setLocation((int)owner.getLocation().getX()+owner.getWidth()/2-this.getWidth()/2, (int)owner.getLocation().getY()+owner.getHeight()/2-this.getHeight()/2);

        getContentPane().setLayout(new GridLayout(2,2));
        getContentPane().add(new JLabel(LABEL_USERNAME));
        getContentPane().add(myUserName);

        getContentPane().add(myConfirm);
        getContentPane().add(myAbort);

        myConfirm.addActionListener(this);
        myAbort.addActionListener(this);
        addWindowListener(this);
    }

    public void actionPerformed(ActionEvent evt)
    {
        if(evt.getSource().equals(myConfirm))
            confirm();
        if(evt.getSource().equals(myAbort))
            abort();
    }

    public void windowClosing(WindowEvent evt)
    {
        abort();
    }

    public void windowOpened(WindowEvent evt){}
    public void windowIconified(WindowEvent evt){}
    public void windowDeiconified(WindowEvent evt){}
    public void windowClosed(WindowEvent evt){}
    public void windowActivated(WindowEvent evt){}
    public void windowDeactivated(WindowEvent evt){}

    public ErgoUserData getData()
    {
        return data;
    }

    private void confirm()
    {
        if(myUserName.getText().length() > 0)
        {
            data = new ErgoUserData(myUserName.getText(), null, null, null, null, myProperties);
            data.save(myProperties);

            this.setVisible(false);
        }
        else
            JOptionPane.showMessageDialog(this, ERROR_USER_EMPTY);
    }

    private void abort()
    {
    	this.setVisible(false);
    }
}
