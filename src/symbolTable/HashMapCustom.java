package symbolTable;

import java.util.ArrayList;

public class HashMapCustom<K,V> {
    private ArrayList<HashNode<K,V>> array;
    private int capacity;
    private int size;

    public HashMapCustom(int dimension){
        array = new ArrayList<>();
        capacity = dimension;
        size = 0;
        for(int i = 0; i < capacity; i++){
            array.add(null);
        }
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size() == 0;
    }

    private int getNode(K key){
        return Math.abs(key.hashCode()) % capacity;
    }

    public V get(K key){
        int index = getNode(key);
        HashNode<K,V> node = array.get(index);

        while(node != null){
            if(node.getKey().equals(key)){
                return node.getValue();
            }
            node = node.getNext();
        }
        return null;
    }

    public void add(String key, V value){
        int index = getNode((K) key);
        HashNode<K,V> pos = array.get(index);

        boolean exists = false;

        while(pos != null && !exists){

            if(pos.getKey().equals(key)){
                pos.setValue(value);
                exists = true;
            }   //if

            pos = pos.getNext();
        }   //while

        if(!exists){
            size++;
            pos = array.get(index);
            HashNode<K,V> new_node = new HashNode<K,V>((K) key, value);
            new_node.setNext(pos);
            array.set(index, new_node);
        }
    }

    public boolean remove(String key){
        int index = getNode((K) key);
        boolean found = false;
        HashNode<K,V> node = array.get(index);
        HashNode<K,V> prev_node = null;

        while(node != null && !found){
            if(node.getKey().equals(key))
                found = true;
            else {
                prev_node = node;
                node = node.getNext();
            }
        }

        if(node == null)
            return false;

        size--;

        if(prev_node != null)
            prev_node.setNext(node.getNext());
        else
            array.set(index, node.getNext());

        return true;
    }
}
