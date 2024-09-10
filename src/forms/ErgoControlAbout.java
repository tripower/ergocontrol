package forms;

import javax.swing.*;

import tools.ErgoTools;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 17.08.2005
 * Time: 07:00:01
 * To change this template use File | Settings | File Templates.
 */
public class ErgoControlAbout extends JDialog implements ActionListener, WindowListener {
	static final long serialVersionUID = 0;

    private static String DIALOGSTRING = "ErgoControl About";
    private static String BUTTON_CONFIRM = "Ok";

    private static String INFORMATION = "ErgoControl " + ErgoTools.VERSION + "\n\rBuild on " + ErgoTools.BUILD_DATE + "\n\nDeveloped by Harald and Klaus Becker\n\nhttp://www.ergocontrol.at.tt";

    private static JTextArea myInformation = new JTextArea(INFORMATION);

    private static int WINDOW_WIDTH = 250;
    private static int WINDOW_HEIGHT = 170;

    private JButton myConfirm = new JButton(BUTTON_CONFIRM);

    public ErgoControlAbout(JFrame owner)
    {
        super(owner, DIALOGSTRING, true);

        setResizable(false);
        setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        setLocation((int)owner.getLocation().getX()+owner.getWidth()/2-this.getWidth()/2, (int)owner.getLocation().getY()+owner.getHeight()/2-this.getHeight()/2);

        myInformation.setEditable(false);
        myInformation.setBackground(getBackground());

        getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
        getContentPane().add(myInformation);

        getContentPane().add(myConfirm);

        myConfirm.addActionListener(this);
        addWindowListener(this);
    }

    public void actionPerformed(ActionEvent evt)
    {
        if(evt.getSource().equals(myConfirm))
            confirm();
    }

    public void windowClosing(WindowEvent evt)
    {
        confirm();
    }

    public void windowOpened(WindowEvent evt){}
    public void windowIconified(WindowEvent evt){}
    public void windowDeiconified(WindowEvent evt){}
    public void windowClosed(WindowEvent evt){}
    public void windowActivated(WindowEvent evt){}
    public void windowDeactivated(WindowEvent evt){}

    private void confirm()
    {
    	this.setVisible(false);
    }
}
