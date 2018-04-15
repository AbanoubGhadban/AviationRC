package com.example.golden.aviationrc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Golden on 3/27/2018.
 */

public class ProblemsListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<ProblemHolder> mProblems;
    private LayoutInflater mInflater;

    public ProblemsListViewAdapter(Context context, List<ProblemHolder> problems) {
        mContext = context;
        mProblems = problems;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mProblems.size();
    }

    @Override
    public Object getItem(int i) {
        return mProblems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ProblemHolder problem = mProblems.get(i);
        view = mInflater.inflate(R.layout.layout_warnings_list_view_item, viewGroup, false);

        TextView problemTV = view.findViewById(R.id.problem_text_tv);
        Button solveBtn = view.findViewById(R.id.btn_solve);

        problemTV.setText(problem.getProblemText());
        solveBtn.setText(problem.getSolveText());
        solveBtn.setOnClickListener(problem.getSolveListener());

        return view;
    }

    public static class ProblemHolder {
        private String mProblemText;
        private String mSolveText;
        private View.OnClickListener mSolveListener;

        public ProblemHolder() {
        }

        public ProblemHolder(String problemText, String solveText, View.OnClickListener solveListener) {
            setProblemText(problemText);
            setSolveText(solveText);
            setSolveListener(solveListener);
        }

        public String getProblemText() {
            return mProblemText;
        }

        public void setProblemText(String mProblemText) {
            this.mProblemText = mProblemText;
        }

        public String getSolveText() {
            return mSolveText;
        }

        public void setSolveText(String mSolveText) {
            this.mSolveText = mSolveText;
        }

        public View.OnClickListener getSolveListener() {
            return mSolveListener;
        }

        public void setSolveListener(View.OnClickListener mSolveListener) {
            this.mSolveListener = mSolveListener;
        }
    }
}
