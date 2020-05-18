/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.bstguesser;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;


public class TreeNode {
    private static final int SIZE = 60;
    private static final int MARGIN = 20;
    private int value, height;
    protected TreeNode left, right, parent;
    private boolean showValue;
    private int x, y;
    private int color = Color.rgb(150, 150, 250);

    public TreeNode(int value) {
        this.value = value;
        this.height = 0; //1;
        showValue = false;
        left = null;
        right = null;
        parent = null;
    }

    public TreeNode insert(int valueToInsert) {
        TreeNode retNode = this;
        TreeNode newNode = new TreeNode(valueToInsert);
        TreeNode parent = this;
        boolean foundInsertPoint = false;
        Log.d("BSTG", "insert start, value=" + valueToInsert);
        while (!foundInsertPoint) {
            if (valueToInsert < parent.value) {
                if (parent.left == null) {
                    foundInsertPoint = true;
                    parent.left = newNode;
                    parent.left.parent = parent;
                    Log.d("BSTG", "insert left");
                }
                else {
                    Log.d("BSTG", "insert search left");
                    parent = parent.left;
                }
            }
            else if (valueToInsert > parent.value) {
                if (parent.right == null) {
                    foundInsertPoint = true;
                    parent.right = newNode;
                    parent.right.parent = parent;
                    Log.d("BSTG", "insert right");
                }
                else {
                    parent = parent.right;
                    Log.d("BSTG", "insert search right");
                }
            }
            else {
                return retNode;
            }
        }

        //
        // Re-calculate node heights
        //
        TreeNode parentIter = parent;
        while (parentIter != null) {
            int height;
            if (parentIter.left == null) {
                height = parentIter.right.height + 1;
            }
            else if (parentIter.right == null) {
                height = parentIter.left.height + 1;
            }
            else {
                height = Math.max(parentIter.left.height, parentIter.right.height) + 1;
            }
            parentIter.height = height;
            parentIter = parentIter.parent;
        }

        //
        // Find first unbalanced ancestor, child and grandchild
        //
        // nodeD : first unbalanced ancestor
        // nodeC : child of nodeD on path to new node
        // nodeB : grandchild of nodeD on path to new node
        //
        parentIter = parent;
        TreeNode nodeD = null;
        TreeNode nodeC = null;
        TreeNode nodeB = null;
        boolean foundUnbalanced = false;
        while (parentIter != null) {
            nodeB = nodeC;
            nodeC = nodeD;
            nodeD = parentIter;

            int leftHeight = 0;
            int rightHeight = 0;
            if (parentIter.left != null) {
                leftHeight = parentIter.left.height;
            }
            if (parentIter.right != null) {
                rightHeight = parentIter.right.height;
            }
            int balanceFactor = rightHeight - leftHeight;
            if (balanceFactor < -1 || balanceFactor > 1) {
                Log.d("BSTD", "rightHeight=" + rightHeight + " leftHeight=" + leftHeight);
                foundUnbalanced = true;
                break;
            }
            parentIter = parentIter.parent;
        }

        if (foundUnbalanced) {
            if (true) {
                if (nodeD == null) {
                    Log.d("BSTG", "nodeD is null");
                }
                if (nodeC == null) {
                    Log.d("BSTG", "nodeC is null");
                }
                if (nodeB == null) {
                    Log.d("BSTG", "nodeB is null");
                }
                Log.d("BSTG", "nodeD.value=" + nodeD.value + " nodeC.value=" + nodeC.value);
            }

            //if (nodeB == null) {
            //    nodeB = newNode;
            //}

            //
            // Re-balance tree
            //
            if (nodeD.left == nodeC) {
                if (nodeC.left == nodeB) {
                    // Left-left case
                    Log.d("BSTG", "Left-Left case");
                    nodeD.left = nodeC.right;
                    if (nodeD.left != null) {
                        nodeD.left.parent = nodeD;
                    }
                    nodeC.right = nodeD;
                    nodeC.parent = nodeD.parent;
                    nodeD.parent = nodeC;
                    if (nodeC.parent != null) {
                        if (nodeC.parent.left == nodeD) {
                            nodeC.parent.left = nodeC;
                        } else {
                            nodeC.parent.right = nodeC;
                        }
                    }
                    nodeD.height -= 1;
                    if (nodeD == this) {
                        retNode = nodeC;
                    }
                }
                else {
                    // Left-Right case
                    Log.d("BSTG", "Left-Right case");
                    nodeD.left = nodeB;
                    nodeB.parent = nodeD;
                    nodeC.right = nodeB.left;
                    if (nodeB.left != null) {
                        nodeB.left.parent = nodeC;
                    }
                    nodeB.left = nodeC;
                    nodeC.parent = nodeB;

                    nodeD.left = nodeB.right;
                    if (nodeD.left != null) {
                        nodeD.left.parent = nodeD;
                    }
                    nodeB.right = nodeD;
                    nodeB.parent = nodeD.parent;
                    nodeD.parent = nodeB;
                    if (nodeB.parent != null) {
                        if (nodeB.parent.left == nodeD) {
                            nodeB.parent.left = nodeB;
                        } else {
                            nodeB.parent.right = nodeB;
                        }
                    }
                    nodeD.height -= 1;
                    nodeC.height -= 1;
                    nodeB.height += 1;
                    if (nodeD == this) {
                        retNode = nodeB;
                    }
                }
            }
            else {
                if (nodeC.left == nodeB) {
                    // Right-Left case
                    Log.d("BSTG", "Right-Left case");
                    nodeD.right = nodeB;
                    nodeB.parent = nodeD;
                    nodeC.left = nodeB.right;
                    if (nodeB.right != null) {
                        nodeB.right.parent = nodeC;
                    }
                    nodeB.right = nodeC;
                    nodeC.parent = nodeB;

                    nodeD.right = nodeB.left;
                    if (nodeD.right != null) {
                        nodeD.right.parent = nodeD;
                    }
                    nodeB.left = nodeD;
                    nodeB.parent = nodeD.parent;
                    nodeD.parent = nodeB;
                    if (nodeB.parent != null) {
                        if (nodeB.parent.left == nodeD) {
                            nodeB.parent.left = nodeB;
                        } else {
                            nodeB.parent.right = nodeB;
                        }
                    }
                    nodeD.height -= 1;
                    nodeC.height -= 1;
                    nodeB.height += 1;
                    if (nodeD == this) {
                        retNode = nodeB;
                    }
                }
                else {
                    // Right-Right case
                    Log.d("BSTG", "Right-Right case");
                    nodeD.right = nodeC.left;
                    if (nodeD.right != null) {
                        nodeD.right.parent = nodeD;
                    }
                    nodeC.left = nodeD;
                    nodeC.parent = nodeD.parent;
                    nodeD.parent = nodeC;
                    if (nodeC.parent != null) {
                        if (nodeC.parent.left == nodeD) {
                            nodeC.parent.left = nodeC;
                        } else {
                            nodeC.parent.right = nodeC;
                        }
                    }
                    nodeD.height -= 1;
                    if (nodeD == this) {
                        retNode = nodeC;
                    }
                }
            }
        }

        Log.d("BSTG", "insert end");
        return retNode;
    }

    public int getValue() {
        return value;
    }

    public void positionSelf(int x0, int x1, int y) {
        this.y = y;
        x = (x0 + x1) / 2;

        if(left != null) {
            left.positionSelf(x0, right == null ? x1 - 2 * MARGIN : x, y + SIZE + MARGIN);
        }
        if (right != null) {
            right.positionSelf(left == null ? x0 + 2 * MARGIN : x, x1, y + SIZE + MARGIN);
        }
    }

    public void draw(Canvas c) {
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3);
        linePaint.setColor(Color.GRAY);
        if (left != null)
            c.drawLine(x, y + SIZE/2, left.x, left.y + SIZE/2, linePaint);
        if (right != null)
            c.drawLine(x, y + SIZE/2, right.x, right.y + SIZE/2, linePaint);

        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(color);
        c.drawRect(x-SIZE/2, y, x+SIZE/2, y+SIZE, fillPaint);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(SIZE * 2/3);
        paint.setTextAlign(Paint.Align.CENTER);
        c.drawText(showValue ? String.valueOf(value) : "?", x, y + SIZE * 3/4, paint);
        // c.drawText(String.valueOf(value), x, y + SIZE * 3/4, paint);

        if (height > 0) {
            Paint heightPaint = new Paint();
            heightPaint.setColor(Color.MAGENTA);
            heightPaint.setTextSize(SIZE * 2 / 3);
            heightPaint.setTextAlign(Paint.Align.LEFT);
            c.drawText(String.valueOf(height), x + SIZE / 2 + 10, y + SIZE * 3 / 4, heightPaint);
        }

        if (left != null)
            left.draw(c);
        if (right != null)
            right.draw(c);
    }

    public int click(float clickX, float clickY, int target) {
        int hit = -1;
        if (Math.abs(x - clickX) <= (SIZE / 2) && y <= clickY && clickY <= y + SIZE) {
            if (!showValue) {
                if (target != value) {
                    color = Color.RED;
                } else {
                    color = Color.GREEN;
                }
            }
            showValue = true;
            hit = value;
        }
        if (left != null && hit == -1)
            hit = left.click(clickX, clickY, target);
        if (right != null && hit == -1)
            hit = right.click(clickX, clickY, target);
        return hit;
    }

    public void invalidate() {
        color = Color.CYAN;
        showValue = true;
    }
}
