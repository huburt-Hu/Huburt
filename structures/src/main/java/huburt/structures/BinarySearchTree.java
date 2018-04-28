package huburt.structures;

/**
 * Created by hubert on 2018/4/28.
 * <p>
 * 简版二叉树
 */
public class BinarySearchTree<T extends Comparable<? super T>> {

    private Node<T> root;

    public BinarySearchTree() {
        root = null;
    }

    public void makeEmpty() {
        root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public boolean contains(T t) {
        return contains(t, root);
    }

    public T findMin() {
        if (isEmpty()) {
            throw new IllegalStateException();
        }
        return findMin(root).element;
    }

    public T findMax() {
        if (isEmpty()) {
            throw new IllegalStateException();
        }
        return findMax(root).element;
    }

    public void insert(T t) {
        root = insert(t, root);
    }

    public void remove(T t) {
        root = remove(t, root);
    }

    public void printTree() {
        if (isEmpty()) {
            System.out.println("Empty Tree");
        } else {
            printTree(root);
        }
    }

    private void printTree(Node<T> node) {
        //中序遍历
        if (root != null) {
            printTree(node.left);
            System.out.println(node.element);
            printTree(node.right);
        }
    }


    private boolean contains(T t, Node<T> node) {
        if (node == null) {
            return false;
        }
        int result = t.compareTo(node.element);
        if (result < 0) {
            return contains(t, node.left);
        } else if (result > 0) {
            return contains(t, node.right);
        } else {
            return true;
        }
    }

    private Node<T> findMin(Node<T> node) {
        //使用递归方式
        if (node == null) {
            return null;
        } else if (node.left == null) {
            return node;
        } else {
            return findMin(node.left);
        }
    }

    private Node<T> findMax(Node<T> node) {
        //使用循环方式
        if (node != null) {
            while (node.right != null) {
                node = node.right;
            }
        }
        return node;
    }

    private Node<T> insert(T t, Node<T> node) {
        if (node == null) {
            return new Node<>(t, null, null);
        }
        int result = t.compareTo(node.element);
        if (result < 0) {
            node.left = insert(t, node.left);
        } else if (result > 0) {
            node.right = insert(t, node.right);
        } else {
            ;//重复，do nothing
        }
        return balance(node);
    }

    private Node<T> remove(T t, Node<T> node) {
        if (node == null) {
            return node;//item not found, do nothing
        }
        int i = t.compareTo(node.element);
        if (i < 0) {
            node.left = remove(t, node.left);
        } else if (i > 0) {
            node.right = remove(t, node.right);
        } else if (node.left != null && node.right != null) {//two children
            node.element = findMin(node.right).element;
            node.right = remove(node.element, node.right);
        } else {
            node = node.left != null ? node.left : node.right;
        }
        return balance(node);
    }

    /**************************************AVL树支持*********************************************/

    private static final int ALLOWED_IMBALANCE = 1;

    private int height(Node<T> node) {
        return node == null ? -1 : node.height;
    }

    private Node<T> balance(Node<T> node) {
        if (node == null) {
            return node;
        }
        if (height(node.left) - height(node.right) > ALLOWED_IMBALANCE) {
            if (height(node.left.left) >= height(node.left.right)) {
                node = rotateWithLeftChild(node);
            } else {
                node = doubleWithLeftChild(node);
            }
        } else if (height(node.right) - height(node.left) > ALLOWED_IMBALANCE) {
            if (height(node.right.right) >= height(node.right.left)) {
                node = rotateWithRightChild(node);
            } else {
                node = doubleWithRightChild(node);
            }
        }
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        return node;
    }

    private Node<T> rotateWithLeftChild(Node<T> k2) {
        Node<T> k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
        k1.height = Math.max(height(k1.left), k2.height) + 1;
        return k1;
    }

    private Node<T> doubleWithLeftChild(Node<T> k3) {
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }

    private Node<T> rotateWithRightChild(Node<T> k1) {
        Node<T> k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = Math.max(height(k1.left), height(k2.right)) + 1;
        k2.height = Math.max(k1.height, height(k1.right)) + 1;
        return k2;
    }

    private Node<T> doubleWithRightChild(Node<T> k2) {
        k2.right = rotateWithLeftChild(k2.right);
        return rotateWithRightChild(k2);
    }


    private static class Node<T> {
        T element;
        Node<T> left;
        Node<T> right;
        int height;//节点高度，用于平衡树

        Node(T element, Node<T> left, Node<T> right) {
            this.element = element;
            this.left = left;
            this.right = right;
        }
    }

}
