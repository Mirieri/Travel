package com.fravier.travel.wizards.initialconifigs;


import android.content.Intent;
import android.support.v4.app.Fragment;

import com.fravier.travel.Dashboard;

import org.codepond.wizardroid.WizardFlow;
import org.codepond.wizardroid.layouts.BasicWizardLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class WizLayInitConf extends BasicWizardLayout {

    public WizLayInitConf() {
        super();
    }

    //You must override this method and create a wizard flow by
    //using WizardFlow.Builder as shown in this example
    @Override
    public WizardFlow onSetup() {
//        setNextButtonLabel("Advance");
//        setBackButtonLabel("Return");
//        setFinishButtonLabel("Finalize");

        return new WizardFlow.Builder()
                .addStep(StepTowns.class)
                .addStep(StepStations.class)
                .addStep(StepRoutes.class)
                .addStep(StepDrivers.class)
                .addStep(StepVehicles.class)
                .create();
    }

    @Override
    public void onStepChanged() {
        super.onStepChanged();
    }



    @Override
    public void onWizardComplete() {
        getActivity().finish();
        startActivity(new Intent(getActivity().getApplicationContext(), Dashboard.class));
    }
}