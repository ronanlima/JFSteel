package com.markosullivan.wizards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.markosullivan.wizards.wizard.model.AbstractWizardModel;
import com.markosullivan.wizards.wizard.model.CustomerNotaServicoPage;
import com.markosullivan.wizards.wizard.model.CustomerPageContaNoQrCode;
import com.markosullivan.wizards.wizard.model.MixedNotaServicoChoicePage;
import com.markosullivan.wizards.wizard.model.ModelCallbacks;
import com.markosullivan.wizards.wizard.model.MultipleFixedChoicePage;
import com.markosullivan.wizards.wizard.model.Page;
import com.markosullivan.wizards.wizard.model.SingleFixedChoicePage;
import com.markosullivan.wizards.wizard.ui.PageFragmentCallbacks;
import com.markosullivan.wizards.wizard.ui.ReviewFragment;
import com.markosullivan.wizards.wizard.ui.StepPagerStrip;

import java.util.List;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_COMENTARIO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_COLETIVA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_PROTOCOLADA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_DEVE_TIRAR_FOTO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_ENDERECO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LEITURA_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LOCAL_ENTREGA_CORRESP;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_MEDIDOR_EXTERNO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_MEDIDOR_VIZINHO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_NO_QR_CODE_POSSUI_CONTA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_TIPO_CONTA;
import static br.com.home.jfsteelbase.ConstantsUtil.SECOND_DATA_KEY;

public class MainActivityWizard extends FragmentActivity implements
        PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks {

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private boolean mEditingAfterReview;
    private AbstractWizardModel mWizardModel;
    private boolean mConsumePageSelectedEvent;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mPhotoButton;
    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;
    private String tipoConta;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wizard);

        tipoConta = getIntent().getStringExtra(EXTRA_TIPO_CONTA);

        if (tipoConta.equals(getResources().getString(R.string.tipo_conta_normal))) {
            mWizardModel = new WizardContaNormal(this, true);
        } else if (tipoConta.equals(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))
                || tipoConta.equals(getResources().getString(R.string.tipo_conta_desligamento))) {
            mWizardModel = new WizardContaNormal(this, false);
        } else if (tipoConta.equals(getResources().getString(R.string.tipo_conta_nota))) {
            mWizardModel = new WizardNotaServico(this);
        } else {
            mWizardModel = new WizardNoQrCode(this);
        }

        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1, position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPhotoButton = (Button) findViewById(R.id.photo_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size() - 1) {
                    Bundle b = finalizaFluxoWizard();
                    b.putBoolean(EXTRA_DEVE_TIRAR_FOTO, false);
                    getIntent().putExtras(b);
                    setResult(Activity.RESULT_OK, getIntent());
                    finish();
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size() - 1) {
                    Bundle b = finalizaFluxoWizard();
                    b.putBoolean(EXTRA_DEVE_TIRAR_FOTO, true);
                    getIntent().putExtras(b);
                    setResult(Activity.RESULT_OK, getIntent());
                    finish();
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();
//    FIXME *MEMORIZACAO_WIZARD* foi solicitado que o sistema grave a última resposta do wizard, no entanto, como é algo mais trabalhoso, acordar um prazo e/ou valor para que seja feito.
//        mStepPagerStrip.getPageSelectedListener().onPageStripSelected(mWizardModel.getSizePageList());
    }

    private Bundle finalizaFluxoWizard() {
        if (tipoConta.equals(getResources().getString(R.string.tipo_conta_normal))) {
            return finalizaFluxoContaNormal(true);
        } else if (tipoConta.equals(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))
                || tipoConta.equals(getResources().getString(R.string.tipo_conta_desligamento))) {
            return finalizaFluxoContaNormal(false);
        } else if (tipoConta.equals(getResources().getString(R.string.tipo_conta_nota))) {
            return finalizaFluxoNotaServico();
        }
        return finalizaFluxoNoQrCode();
    }

    private Bundle finalizaFluxoContaNormal(boolean devePegarSegundaTela) {
        Bundle b = new Bundle();
        SingleFixedChoicePage p = (SingleFixedChoicePage) mWizardModel.getPageList().get(0);
        b.putString(EXTRA_LOCAL_ENTREGA_CORRESP, p.getData().getString(p.SIMPLE_DATA_KEY));

        if (devePegarSegundaTela) {
            MultipleFixedChoicePage p1 = (MultipleFixedChoicePage) mWizardModel.getPageList().get(1);
            if (p1.getData().getStringArrayList(p1.SIMPLE_DATA_KEY) != null && !p1.getData().getStringArrayList(p1.SIMPLE_DATA_KEY).isEmpty()) {
                for (String op : p1.getData().getStringArrayList(p1.SIMPLE_DATA_KEY)) {
                    if (op.equals(WizardContaNormal.choicesSobreConta[0])) {
                        b.putBoolean(EXTRA_CONTA_PROTOCOLADA, true);
                    } else if (op.equals(WizardContaNormal.choicesSobreConta[1])) {
                        b.putBoolean(EXTRA_CONTA_COLETIVA, true);
                    }
                }
            }
        }
        return b;
    }

    private Bundle finalizaFluxoNotaServico() {
        Bundle b = new Bundle();
        CustomerNotaServicoPage p = (CustomerNotaServicoPage) mWizardModel.getPageList().get(0);
        if (p.getData().getString(EXTRA_LEITURA_DATA_KEY) != null && !p.getData().getString(EXTRA_LEITURA_DATA_KEY).trim().isEmpty()) {
            b.putString(EXTRA_LEITURA_DATA_KEY, p.getData().getString(EXTRA_LEITURA_DATA_KEY));
        }
        if (p.getData().getString(EXTRA_MEDIDOR_VIZINHO_DATA_KEY) != null && !p.getData().getString(EXTRA_MEDIDOR_VIZINHO_DATA_KEY).trim().isEmpty()) {
            b.putString(EXTRA_MEDIDOR_VIZINHO_DATA_KEY, p.getData().getString(EXTRA_MEDIDOR_VIZINHO_DATA_KEY));
        }
        MixedNotaServicoChoicePage p2 = (MixedNotaServicoChoicePage) mWizardModel.getPageList().get(1);
        if (p2.getData().getString(p2.SIMPLE_DATA_KEY) != null && !p2.getData().getString(p2.SIMPLE_DATA_KEY).trim().isEmpty()) {
            b.putString(EXTRA_LOCAL_ENTREGA_CORRESP, p2.getData().getString(p2.SIMPLE_DATA_KEY));
        }
        if (p2.getData().getString(SECOND_DATA_KEY) != null && !p2.getData().getString(SECOND_DATA_KEY).trim().isEmpty()) {
            b.putString(EXTRA_MEDIDOR_EXTERNO, p2.getData().getString(SECOND_DATA_KEY));
        }
        return b;
    }

    private Bundle finalizaFluxoNoQrCode() {
        Bundle b = new Bundle();
        CustomerPageContaNoQrCode p = (CustomerPageContaNoQrCode) mWizardModel.getPageList().get(0);
        if (p.getData().getString(EXTRA_LEITURA_DATA_KEY) != null && !p.getData().getString(EXTRA_LEITURA_DATA_KEY).trim().isEmpty()) {
            b.putString(EXTRA_LEITURA_DATA_KEY, p.getData().getString(EXTRA_LEITURA_DATA_KEY));
        }
        if (p.getData().getString(EXTRA_ENDERECO_DATA_KEY) != null && !p.getData().getString(EXTRA_ENDERECO_DATA_KEY).trim().isEmpty()) {
            b.putString(EXTRA_ENDERECO_DATA_KEY, p.getData().getString(EXTRA_ENDERECO_DATA_KEY));
        }
        if (p.getData().getString(EXTRA_COMENTARIO_DATA_KEY) != null && !p.getData().getString(EXTRA_COMENTARIO_DATA_KEY).trim().isEmpty()) {
            b.putString(EXTRA_COMENTARIO_DATA_KEY, p.getData().getString(EXTRA_COMENTARIO_DATA_KEY));
        }
        if (p.getData().getStringArrayList(p.SIMPLE_DATA_KEY) != null && !p.getData().getStringArrayList(p.SIMPLE_DATA_KEY).isEmpty()) {
            for (String op : p.getData().getStringArrayList(p.SIMPLE_DATA_KEY)) {
                if (op.equals(WizardNoQrCode.choicesResidencias[0])) {
                    b.putString(EXTRA_NO_QR_CODE_POSSUI_CONTA, "Sim");
                    break;
                }
            }
        }
        return b;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED, new Intent());
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size()); // + 1 = review step
//        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 = review step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size() - 1) {
            mNextButton.setText(R.string.finish);
            if (!tipoConta.equals(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))) {
                mPhotoButton.setVisibility(View.VISIBLE);
            }
        } else {
            mPhotoButton.setVisibility(View.GONE);
            mNextButton.setText(mEditingAfterReview
                    ? R.string.review
                    : R.string.next);
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
//        int cutOffPage = mCurrentPageSequence.size() + 1;
        int cutOffPage = mCurrentPageSequence.size();
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
//            if (i >= mCurrentPageSequence.size()) {
//                return new ReviewFragment();
//            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            if (mCurrentPageSequence == null) {
                return 0;
            }
            return mCurrentPageSequence.size();
//            return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }
}
