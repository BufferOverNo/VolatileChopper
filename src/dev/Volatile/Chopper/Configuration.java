package dev.Volatile.Chopper;

import org.osbot.rs07.api.ui.RS2Widget;

public class Configuration {

	private boolean isHideUsername;
	private RS2Widget widget;
	private int usernameLength;
	private int usernameX;

	public boolean isHideUsername() {
		return isHideUsername;
	}

	public void setHideUsername(boolean isHideUsername) {
		this.isHideUsername = isHideUsername;
	}

	public RS2Widget getWidget() {
		return widget;
	}

	public void setWidget(RS2Widget widget) {
		this.widget = widget;
	}

	public int getUsernameLength() {
		return usernameLength;
	}

	public void setUsernameLength(int usernameLength) {
		this.usernameLength = usernameLength;
	}

	public int getUsernameX() {
		return usernameX;
	}

	public void setUsernameX(int usernameX) {
		this.usernameX = usernameX;
	}

}
