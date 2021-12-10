package com.samb.trs.Systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.samb.trs.Components.CollisionComponent;
import com.samb.trs.Components.TextureComponent;
import com.samb.trs.Components.TypeComponent;
import com.samb.trs.Controllers.MainController;
import com.samb.trs.Factories.EntityFactory;
import com.samb.trs.Model.Score;
import com.samb.trs.Resources.Sounds;
import com.samb.trs.Utilities.Mappers;

import static com.samb.trs.Components.TypeComponent.*;
import static com.samb.trs.Factories.EntityFactory.compare;
import static com.samb.trs.Resources.Constants.Collision.BOOST_PROBABILITY;

public class CollisionSystem extends IteratingSystem {
    private MainController mainController;
    private Score score;

    @SuppressWarnings("unchecked")
    public CollisionSystem(MainController mainController, Score score) {
        super(Family.all(CollisionComponent.class, TypeComponent.class).get());
        this.mainController = mainController;
        this.score = score;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollisionComponent cc = Mappers.collision.get(entity);
        TypeComponent tc = Mappers.type.get(entity);

        Entity collidedEntity = cc.collisionEntity;
        if (collidedEntity != null) {
            if (Mappers.type.has(collidedEntity)) {
                TypeComponent type_collided = Mappers.type.get(collidedEntity);

                Body body1;
                Body body2;

                Vector2 contactPoint = cc.contact.getWorldManifold().getPoints()[0];

                Entity first, second;

                EntityFactory entityFactory = getEngine().getSystem(SpawnSystem.class).getEntityFactory();

                switch (tc.type | type_collided.type) {
                    case BOAT | FISH:
                    case BOAT | ROCK:
                    case BOAT | SHORE:
                    case BOAT | TRUNK:
                        first = tc.type == BOAT ? entity : collidedEntity;
                        if (!Mappers.shield.get(first).isProtected) {
                            //mainController.getSoundController().queueSound(first, Sounds.CRASH);
                            getEngine().getSystem(GameLogicSystem.class).setGameState(GameLogicSystem.GameState.GAMEOVER);
                        }
                        break;
                    case ROCK:
                        first = entity;
                        second = collidedEntity;

                        body1 = Mappers.body.get(entity).body;
                        body2 = Mappers.body.get(collidedEntity).body;

                        float c = compare(first, second);

                        if (c > 0) {
                            TextureComponent tex = Mappers.texture.get(second);
                            entityFactory.resize(second, tex.width * 0.8f, tex.height * 0.8f);
                            if (tex.width < 110) {
                                removeEntityAndSpawnCollectEntity(entityFactory, second);
                            }
                        } else if (c < 0) {
                            TextureComponent tex = Mappers.texture.get(first);
                            entityFactory.resize(first, tex.width * 0.8f, tex.height * 0.8f);
                            if (tex.width < 110) {
                                removeEntityAndSpawnCollectEntity(entityFactory, first);
                            }
                        } else {
                            TextureComponent tex = Mappers.texture.get(first);
                            entityFactory.resize(first, tex.width * 0.8f, tex.height * 0.8f);
                            entityFactory.resize(second, tex.width, tex.height);
                            if (tex.width < 110) {
                                removeEntityAndSpawnCollectEntity(entityFactory, first);
                                removeEntityAndSpawnCollectEntity(entityFactory, second);
                            }
                        }

                        mainController.getSoundController().queueSound(first, Sounds.ROCK);

                        break;
                    case ROCK | TRUNK:
                    case ROCK | SHORE:
                        first = tc.type == ROCK ? entity : collidedEntity;
                        mainController.getSoundController().queueSound(first, Sounds.ROCK);
                        break;
                    case TRUNK | COIN:
                    case TRUNK | BOOST:
                        first = tc.type == TRUNK ? entity : collidedEntity;
                        second = tc.type == TRUNK ? collidedEntity : entity;

                        mainController.getSoundController().queueSound(second, Sounds.COIN_DROP);
                        break;
                    case SHORE | COIN:
                    case SHORE | BOOST:
                        second = tc.type == SHORE ? collidedEntity : entity;
                        mainController.getSoundController().queueSound(second, Sounds.COIN_DROP);
                        break;
                    case ROCK | COIN:
                    case ROCK | BOOST:
                        second = tc.type == ROCK ? collidedEntity : entity;

                         mainController.getSoundController().queueSound(second, Sounds.COIN_DROP);
                        break;
                    case SHIELD | ROCK:
                        first = tc.type == SHIELD ? entity : collidedEntity;
                        if (Mappers.shield.get(Mappers.attached.get(first).attachedTo).isProtected) {
                            mainController.getSoundController().queueSound(first, Sounds.SHIELD_HIT);
                        }
                        break;
                    case BOAT | BOOST:
                    case BOAT | COIN:
                    case SHIELD | BOOST:
                    case SHIELD | COIN:
                        first = tc.type == BOOST || tc.type == COIN ? entity : collidedEntity;

                        body1 = Mappers.body.get(first).body;

                        if (Mappers.type.get(first).type == BOOST) {
                            score.increaseBoosts();
                            mainController.getSoundController().queueSound(first, Sounds.BOOST);
                        }
                        if (Mappers.type.get(first).type == COIN) {
                            score.increaseCollectedCoins();
                            mainController.getSoundController().queueSound(first, Sounds.COIN);
                        }
                        Mappers.collect.get(first).isCollected = true;
                        break;
                    case FISH | ROCK:
                    case FISH | TRUNK:
                    case FISH | SHORE:
                        first = tc.type == FISH ? entity : collidedEntity;

                        Mappers.death.get(first).isDead = true;

                        mainController.getSoundController().queueSound(first, Sounds.SPLASH);
                        break;
                    case FISH | SHIELD:
                        first = tc.type == FISH ? entity : collidedEntity;
                        second = tc.type == FISH ? collidedEntity : entity;

                        if (Mappers.shield.get(Mappers.attached.get(second).attachedTo).isProtected) {
                            Mappers.death.get(first).isDead = true;
                            mainController.getSoundController().queueSound(first, Sounds.SPLASH);
                        }
                        break;
                }
                cc.collisionEntity = null;
                if (Mappers.collision.has(collidedEntity))
                    Mappers.collision.get(collidedEntity).collisionEntity = null;
            }
        }

    }

    private void removeEntityAndSpawnCollectEntity(EntityFactory entityFactory, Entity entity) {
        Vector2 pos = Mappers.transform.get(entity).position;

        float rand = MathUtils.random();
        Entity e;

        if (rand > BOOST_PROBABILITY)
            e = entityFactory.makeCoinEntity(pos.x, pos.y, mainController.getRenderController().getDynamicViewport());
        else
            e = entityFactory.makeBoostEntity(pos.x, pos.y, mainController.getRenderController().getDynamicViewport());
        EntityFactory.copyVelocity(entity, e);

//        entity.remove(BodyComponent.class);
//        entity.remove(TextureComponent.class);
//        getEngine().removeEntity(entity);
        Mappers.death.get(entity).isDead = true;

        getEngine().addEntity(e);

        TextureComponent tc = Mappers.texture.get(e);
        mainController.getSoundController().queueSound(e, Sounds.COIN_DROP);
    }

}