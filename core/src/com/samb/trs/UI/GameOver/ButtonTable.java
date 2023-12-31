package com.samb.trs.UI.GameOver;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.samb.trs.Controllers.MainController;
import com.samb.trs.Controllers.RenderController;
import com.samb.trs.Resources.TextureRegions;

public class ButtonTable extends Table {
    private ImageButton restartButton, menuButton;

    public ButtonTable(MainController mainController) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(mainController.getAssetController().getAsset(TextureRegions.REWIND_BUTTON));
        TextureRegionDrawable drawable2 = new TextureRegionDrawable(mainController.getAssetController().getAsset(TextureRegions.MENU_BUTTON));

        this.restartButton = new ImageButton(drawable, drawable);
        this.menuButton = new ImageButton(drawable2, drawable2);

        add(restartButton).padRight(50).size(RenderController.wperc(15), RenderController.wperc(15));
        add(menuButton).padLeft(50).size(RenderController.wperc(15), RenderController.wperc(15));
    }

    public ImageButton getRestartButton() {
        return restartButton;
    }

    public ImageButton getMenuButton() {
        return menuButton;
    }
}
