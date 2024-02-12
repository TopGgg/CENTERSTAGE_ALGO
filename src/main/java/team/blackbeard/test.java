package team.blackbeard;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class test {

    public static class hello {
        public String param = "Hello";

        @Override
        public String toString(){
            return param;
        }
    }

    public static void main(String[] args) {
        ArrayList<hello> hellos = new ArrayList<>();
        hellos.add(new hello());
        hellos.add(new hello());
        hellos.add(new hello());

        System.out.println(hellos.stream().map(Object::toString)
                .collect(Collectors.joining(", ")));

        hellos.get(0).param = "hell";

        System.out.println(hellos.stream().map(Object::toString)
                .collect(Collectors.joining(", ")));
    }
}
