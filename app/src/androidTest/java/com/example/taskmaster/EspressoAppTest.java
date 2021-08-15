package com.example.taskmaster;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.taskmaster.DB.TaskDao;
import com.example.taskmaster.DB.TaskDatabase;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.contrib.RecyclerViewActions;

////
import static com.google.android.material.datepicker.CompositeDateValidator.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import java.util.Map;



@RunWith(AndroidJUnit4.class)
public class EspressoAppTest {


    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);


    @Test
    public void test_The_Main_Activity(){
        onView(withId(R.id.textView)).check(matches(withText("My Tasks")));
    }

    @Test
    public void test_The_Setting_Activity(){
        onView(withId(R.id.setting)).perform(click());
        onView(withId(R.id.settingTitle)).check(matches(withText("Setting")));

    }

    @Test
    public void test_The_Add_Task_Activity(){
        onView(withId(R.id.addTask)).perform(click());
        onView(withId(R.id.addTaskTitle)).check(matches(withText("Add Task")));

    }

    @Test
    public void test_The_All_Task_Activity(){
        onView(withId(R.id.allTask)).perform(click());
        onView(withId(R.id.allTaskTitle)).check(matches(withText("All Task")));

    }

    @Test
    public void test_Task_Details_Activity(){
        onView(withId(R.id.allTask)).perform(click());
        onView(withId(R.id.allTaskTitle)).check(matches(withText("All Task")));

    }

    @Test
    public void test_Add_New_Task(){
        onView(withId(R.id.addTask)).perform(click());
        onView(withId(R.id.task_title_input)).perform(typeText("new task 1"),closeSoftKeyboard());
        onView(withId(R.id.task_body_input)).perform(typeText("task details"),closeSoftKeyboard());

//        onView(withId(R.id.spinner_status)).perform(click());
//        onView(withSpinnerText("new")).perform(click());

        onView(withId(R.id.submit_Button_AddTask)).perform(click());
        pressBack();

        onView(ViewMatchers.withId(R.id.recycler_task)).check(matches(isDisplayed()));
    }

    @Test
    public void test_open_Task(){
        onView(ViewMatchers.withId(R.id.recycler_task)).check(matches(isDisplayed()));

        onView(withId(R.id.recycler_task))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        onView(withId(R.id.singleTaskTitle)).check(matches(withText("new task 1")));
        onView(withId(R.id.task_body)).check(matches(withText("task details")));
        onView(withId(R.id.task_status)).check(matches(withText("new")));
    }

    @Test
    public void test_Change_userName(){
        onView(withId(R.id.setting)).perform(click());
        onView(withId(R.id.settingTitle)).check(matches(withText("Setting")));
        onView(withId(R.id.updateFormUserName)).perform(typeText("Mohammad"),closeSoftKeyboard());
        onView(withId(R.id.updateUserNameButton)).perform(click());
        pressBack();
        onView(withId(R.id.usernameInHomePage)).check(matches(withText("Mohammad's Tasks")));

    }


}
