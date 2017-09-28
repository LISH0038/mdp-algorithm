package controller;

import view.Simulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by koallen on 29/9/17.
 */
public class RealRunCheckBoxListener implements ActionListener {

    private Simulator mView;

    public RealRunCheckBoxListener(Simulator view) {
        mView = view;
        mView.addRealRunCheckBoxListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (mView.getIsRealRun()) {
            mView.disableLoadMapButton();
        } else {
            mView.enableLoadMapButton();
        }
    }
}
