package com.samb.trs.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.samb.trs.Controllers.AssetController;
import com.samb.trs.Controllers.MainController;
import com.samb.trs.Interfaces.UIElement;
import com.samb.trs.Resources.TextureRegions;

public abstract class UIWindow extends Table implements UIElement {
    protected MainController mainController;
    protected AssetController assets;
    protected Color labelColor;
    protected Viewport viewport;
    private boolean isHidden;

    public UIWindow(MainController mainController, float width, float height) {
        super();
        this.mainController = mainController;
        this.assets = mainController.getAssetController();
        this.viewport = mainController.getRenderController().getStaticViewport();
        isHidden = true;

        labelColor = new Color(1, 0.847f, 0.584f, 1);
        setBackground(new TextureRegionDrawable(mainController.getAssetController().getAsset(TextureRegions.SCOREBOARD)));
        setBounds(viewport.getWorldWidth() / 2f - width / 2f, viewport.getWorldHeight() / 2f - width / 2, width, height);
        center();
    }

    public void show(Runnable runnable, float duration, Interpolation interpolation) {
        if (isHidden) {
            onShow(runnable, duration, interpolation);
            isHidden = false;
            setTouchable(Touchable.enabled);
        }
    }

    public void hide(Runnable runnable, float duration, Interpolation interpolation) {
        if (!isHidden) {
            onHide(runnable, duration, interpolation);
            isHidden = true;
            setTouchable(Touchable.disabled);
        }
    }

    protected float getCenterX(){
        return 0.5f * (viewport.getWorldWidth() - getWidth());
    }

    protected float getCenterY(){
        return 0.5f * (viewport.getWorldHeight() - getHeight());
    }

    protected abstract void resetWindow();

    protected abstract void onShow(Runnable runnable, float duration, Interpolation interpolation);

    protected abstract void onHide(Runnable runnable, float duration, Interpolation interpolation);

    public boolean isHidden() {
        return isHidden;
    }
}