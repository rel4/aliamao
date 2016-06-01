package com.aliamauri.meat.eventBus;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
/**
 *发布动态类的 从activity传到serivce中的数据模型
 * @author limaokeji-windosc
 *
 */
public class BroadCastDataModel implements Parcelable {
	
	private  ArrayList<String> albumLists;  //用户上传图片的集合
	private ArrayList<String> tagLists;   //用户的标签集合
	private String editText;    //用户发布的文字信息
	private String isAnonymous;//当前用户是否匿名发布
	

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringList(albumLists);
		dest.writeStringList(tagLists);
		dest.writeString(editText);
		dest.writeString(isAnonymous);
	}
	
	public BroadCastDataModel() {
		super();
	}

	public BroadCastDataModel(Parcel source) {
		albumLists = source.createStringArrayList();
		tagLists = source.createStringArrayList();
		editText = source.readString();
		isAnonymous = source.readString();
		
	}
	public static final Parcelable.Creator<BroadCastDataModel> CREATOR = new Creator<BroadCastDataModel>() {
		@Override
		public BroadCastDataModel createFromParcel(Parcel source) {
			return new BroadCastDataModel(source);
		}
		@Override
		public BroadCastDataModel[] newArray(int size) {
			return new BroadCastDataModel[size];
		}
	};
	
	public ArrayList<String> getAlbumLists() {
		return albumLists;
	}

	public void setAlbumLists(ArrayList<String> albumLists) {
		this.albumLists = albumLists;
	}

	public ArrayList<String> getTagLists() {
		return tagLists;
	}

	public void setTagLists(ArrayList<String> tagLists) {
		this.tagLists = tagLists;
	}

	public String getEditText() {
		return editText;
	}

	public void setEditText(String editText) {
		this.editText = editText;
	}

	public String getIsAnonymous() {
		return isAnonymous;
	}

	public void setIsAnonymous(String isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}





















