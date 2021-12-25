package com.example.bmi;

public class Food {
    private String foodName;
    private String category;
    private String calorie;
    private String img_URI;
    private String foodId;
    private String userId;
    private int selectedIdItem;


    public Food() {
    }

    public int getSelectedIdItem() {
        return selectedIdItem;
    }

    public void setSelectedIdItem(int selectedIdItem) {
        this.selectedIdItem = selectedIdItem;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getImg_URI() {
        return img_URI;
    }

    public void setImg_URI(String img_URI) {
        this.img_URI = img_URI;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }
}
